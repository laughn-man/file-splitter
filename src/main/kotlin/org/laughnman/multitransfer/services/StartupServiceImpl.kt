package org.laughnman.multitransfer.services

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.laughnman.multitransfer.models.*
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.SourceReader
import org.laughnman.multitransfer.services.transfer.TransferDestinationService
import org.laughnman.multitransfer.utilities.TransferMonitor
import picocli.CommandLine
import java.nio.ByteBuffer
import kotlin.system.exitProcess

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

	private fun CoroutineScope.buildJob(bufferSize: ChunkSize, transferDestinationService: TransferDestinationService,
											 sourceReader: SourceReader): Job {
		val job = launch {

			lateinit var metaInfo: MetaInfo
			lateinit var transferMonitor: TransferMonitor

			val destinationWriter = transferDestinationService.write()
			val buffer = ByteBuffer.allocateDirect(bufferSize.toBytes().toInt())

			sourceReader(buffer).collect { transfer ->
				when (transfer) {
					is Start -> {
						metaInfo = transfer.metaInfo
						transferMonitor = TransferMonitor(metaInfo.fileName)
						transferMonitor.start()
						logger.info { "${metaInfo.fileName}: Starting transfer job." }
						destinationWriter(buffer, transfer)
					}
					is BufferReady -> {
						val bytesRead = buffer.position()
						// Reset the buffer so it is ready for reading.
						buffer.flip()
						destinationWriter(buffer, transfer)
						transferMonitor.addTime(bytesRead)
						// Clear out the buffer so it is ready to be written to again.
						buffer.clear()
						transferMonitor.printTransferMessage()
					}
					is Complete -> {
						destinationWriter(buffer, transfer)
						transferMonitor.stop()
						logger.info { "${metaInfo.fileName}: Finishing transfer job." }
					}
					is Error -> {
						destinationWriter(buffer, transfer)
						transferMonitor.stop()
						logger.error(transfer.exception) { "${metaInfo.fileName}: Exception occurred in transfer job." }
					}
				}
			}

			transferMonitor.printTotalTransferMessage()
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

			transferSourceService.read().collect { sourceReader ->
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

				val job = buildJob(transferCommand.bufferSize, transferDestinationService, sourceReader)

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

		val transferSourceCommands = arrayOf(FileSourceCommand(), ArtifactorySourceCommand(), S3SourceCommand())
		val transferDestinationCommands = arrayOf(FileDestinationCommand(), ArtifactoryDestinationCommand(), S3DestinationCommand())

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