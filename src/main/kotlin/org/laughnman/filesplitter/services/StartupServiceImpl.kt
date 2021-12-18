package org.laughnman.filesplitter.services

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.laughnman.filesplitter.models.*
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import picocli.CommandLine
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

private const val SLEEP_TIME = 100L

class StartupServiceImpl(private val fileSplitterService: FileSplitterService,
												 private val transferFactoryService: TransferFactoryService
) : StartupService {

	private fun runSplit(command: SplitCommand) {
		logger.debug { "Calling runSplit command: $command" }
		fileSplitterService.splitFiles(command)
	}

	private fun runCombine(command: CombineCommand) {
		logger.debug { "Calling runCombine command: $command" }
		fileSplitterService.combineFiles(command)
	}

	private fun runTransfer(transferCommand: TransferCommand, sourceCommands: Array<out AbstractCommand>, destinationCommands: Array<out AbstractCommand>) {
		logger.debug { "Calling runTransfer sourceCommands: $sourceCommands, destinationCommands: $destinationCommands" }

		val sourceCommand = sourceCommands.first { it.called }
		val destinationCommand = destinationCommands.first { it.called }

		runBlocking {
			val transferSourceService = transferFactoryService.getSourceService(this, sourceCommand)
			val transferDestinationService = transferFactoryService.getDestinationService(this, destinationCommand)

			val processBuffer = ArrayList<Job>(transferCommand.parallel)

			transferSourceService.read().collect { (metaInfo, channel) ->

				// Special optimized case for only one process at a time.
				if (transferCommand.parallel == 1) {
					transferDestinationService.write(metaInfo, channel)
				}
				else {
					// If the process buffer is full then loop until a job finishes.
					while (processBuffer.size == transferCommand.parallel) {
						// Wait for a bit to see if a job frees up.
						delay(SLEEP_TIME)

						// Loop backwards so items can be removed from the list without causing issues.
						for (i in processBuffer.indices.reversed()) {
							if (processBuffer[i].isCompleted) {
								processBuffer.removeAt(i)
							}
						}
					}

					processBuffer.add(launch { transferDestinationService.write(metaInfo, channel) })
				}
			}

			// Wait on the remaining jobs.
			for (job in processBuffer) {
				job.join()
			}
		}
	}

	override fun run(args: Array<String>) {
		logger.info { "Starting Universal Transfer." }

		val splitCommand = SplitCommand()
		val combineCommand = CombineCommand()
		val transferCommand = TransferCommand()

		val transferSourceCommands = arrayOf(FileSourceCommand())
		val transferDestinationCommands = arrayOf(FileDestinationCommand())

		val transferCommandLine = CommandLine(transferCommand)
		transferSourceCommands.forEach { transferCommandLine.addSubcommand(it) }
		transferDestinationCommands.forEach { transferCommandLine.addSubcommand(it) }

		val returnCode = CommandLine(MainCommand())
			.setExecutionStrategy(CommandLine.RunAll())
			.addSubcommand(splitCommand)
			.addSubcommand(combineCommand)
			.addSubcommand(transferCommandLine)
			.execute(*args)

		if (returnCode != 0) {
			exitProcess(returnCode)
		}

		if (splitCommand.called) {
			runSplit(splitCommand)
		}
		else if (combineCommand.called) {
			runCombine(combineCommand)
		}
		else if (transferCommand.called) {
			runTransfer(transferCommand, transferSourceCommands, transferDestinationCommands)
		}
	}
}