package org.laughnman.multitransfer.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.laughnman.multitransfer.models.*
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.TransferDestinationService
import org.laughnman.multitransfer.utilities.TransferMonitor
import picocli.CommandLine
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

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
			val transferMonitor = TransferMonitor()

			val block = transferDestinationService.write()
			flow.collect { transfer ->
				when (transfer) {
					is Start -> {
						metaInfo = transfer.metaInfo
						transferMonitor.start()
						logger.info { "${transfer.metaInfo.fileName}: Starting transfer job." }
						block(transfer)
					}
					is Next -> {
						block(transfer)
						transferMonitor.addTransferRecord(transfer.bytesRead)
						logger.info { "${transfer.metaInfo.fileName}: Transferred ${transfer.bytesRead} at ${transferMonitor.calculateMegaBytesPerSecond()} MB/s." }
					}
					is Complete -> {
						block(transfer)
						transferMonitor.stop()
						logger.info { "${transfer.metaInfo.fileName}: Finishing transfer job." }
					}
					is Error -> {
						block(transfer)
						transferMonitor.stop()
						logger.error(transfer.exception) { "${transfer.metaInfo.fileName}: Exception occurred in transfer job." }
					}
				}
			}

			val timeStr = transferMonitor.calculateTotalRunTime().toComponents { hours, minutes, seconds, nanoseconds ->
				"$hours:$minutes:$seconds.$nanoseconds"
			}

			logger.info { "Transfer job for file ${metaInfo!!.fileName} complete, runtime $timeStr at ${transferMonitor.calculateTotalMegaBytesPerSecond()} MB/s." }
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