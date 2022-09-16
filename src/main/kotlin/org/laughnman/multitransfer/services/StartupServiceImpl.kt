package org.laughnman.multitransfer.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.laughnman.multitransfer.models.*
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.TransferDestinationService
import picocli.CommandLine
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

private const val SLEEP_TIME = 100L

class StartupServiceImpl(private val fileSplitterService: FileSplitterService,
	private val transferFactoryService: TransferFactoryService) : StartupService {

	private fun runSplit(command: SplitCommand) {
		logger.debug { "Calling runSplit command: $command" }
		fileSplitterService.splitFiles(command)
	}

	private fun runCombine(command: CombineCommand) {
		logger.debug { "Calling runCombine command: $command" }
		fileSplitterService.combineFiles(command)
	}

	@OptIn(ExperimentalTime::class)
	private fun CoroutineScope.buildJob(transferDestinationService: TransferDestinationService,
											 flow: Flow<Transfer>): Job {
		val job = launch {

			var metaInfo: MetaInfo? = null

			val elapsed = measureTime {
				val block = transferDestinationService.write()
				flow.collect { transfer ->
					when (transfer) {
						is Start -> {
							logger.info { "Starting transfer job for file ${transfer.metaInfo.fileName}." }
							block(transfer)
						}
						is Next -> {
							block(transfer)
						}
						is Complete -> {
							block(transfer)
							logger.info { "Finishing transfer job for file ${transfer.metaInfo.fileName}." }
						}
						is Error -> {
							block(transfer)
							logger.error(transfer.exception) { "Exception occured in transfer job for file ${transfer.metaInfo.fileName}." }
						}
					}

				}
			}

			val timeStr = elapsed.toComponents { hours, minutes, seconds, nanoseconds ->
				"$hours:$minutes:$seconds.$nanoseconds"
			}

			logger.info { "Transfer job for file ${metaInfo!!.fileName} complete, runtime $timeStr" }
		}

		return job
	}

	private fun runTransfer(transferCommand: TransferCommand, sourceCommands: Array<out AbstractCommand>, destinationCommands: Array<out AbstractCommand>) {
		logger.debug { "Calling runTransfer sourceCommands: $sourceCommands, destinationCommands: $destinationCommands" }

		val sourceCommand = sourceCommands.first { it.called }
		val destinationCommand = destinationCommands.first { it.called }

		runBlocking {
			val transferSourceService = transferFactoryService.getSourceService(sourceCommand)
			val transferDestinationService = transferFactoryService.getDestinationService(destinationCommand)

			val processBuffer = ArrayList<Job>(transferCommand.parallelism)

			transferSourceService.read().collect { flow ->
				// If the process buffer is full then loop until a job finishes.
				while (processBuffer.size == transferCommand.parallelism) {
					// Wait for a bit to see if a job frees up.
					delay(SLEEP_TIME)

					// Loop backwards so items can be removed from the list without causing issues.
					for (i in processBuffer.indices.reversed()) {
						val job = processBuffer[i]
						if (job.isCompleted) {
							processBuffer.removeAt(i)
						}
					}
				}

				val job = buildJob(transferDestinationService, flow)

				// Add the job to the process buffer.
				processBuffer.add(job)
			}

			// Wait on the remaining jobs.
			for (job in processBuffer) {
				job.join()
			}
		}
	}

	override fun run(args: Array<String>) {
		logger.info { "Starting Multi-Transfer." }

		val splitCommand = SplitCommand()
		val combineCommand = CombineCommand()
		val transferCommand = TransferCommand()

		val transferSourceCommands = arrayOf(FileSourceCommand(), ArtifactorySourceCommand())
		val transferDestinationCommands = arrayOf(FileDestinationCommand(), ArtifactoryDestinationCommand())

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