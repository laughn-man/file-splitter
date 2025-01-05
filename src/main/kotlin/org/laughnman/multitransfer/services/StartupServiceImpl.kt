package org.laughnman.multitransfer.services

import io.github.oshai.kotlinlogging.KotlinLogging
import org.laughnman.multitransfer.models.*
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.TransferService
import picocli.CommandLine

private val logger = KotlinLogging.logger {}

class StartupServiceImpl(private val fileSplitterService: FileSplitterService,
	private val transferService: TransferService) : StartupService {

	private fun runSplit(command: SplitCommand) {
		logger.debug { "Calling runSplit command: $command" }
		fileSplitterService.splitFiles(command)
	}

	private fun runCombine(command: CombineCommand) {
		logger.debug { "Calling runCombine command: $command" }
		fileSplitterService.combineFiles(command)
	}

	private fun runTransfer(transferCommand: TransferCommand, sourceCommands: Array<out AbstractCommand>, destinationCommands: Array<out AbstractCommand>): Int {
		logger.debug { "Call runTransfer command: $transferCommand" }

		val sourceCommand = sourceCommands.firstOrNull { it.called }
		val destinationCommand = destinationCommands.firstOrNull() { it.called }

		if (sourceCommand != null && destinationCommand != null) {
			transferService.runTransfer(transferCommand, sourceCommand, destinationCommand)
			return 0
		}

		return 100

	}

	override fun run(args: Array<String>): Int {
		logger.info { "Starting Multi-Transfer." }

		val splitCommand = SplitCommand()
		val combineCommand = CombineCommand()
		val transferCommand = TransferCommand()

		val transferSourceCommands = arrayOf(FileSourceCommand(), ArtifactorySourceCommand(), S3SourceCommand())
		val transferDestinationCommands = arrayOf(FileDestinationCommand(), ArtifactoryDestinationCommand(), S3DestinationCommand())

		val transferCommandLine = CommandLine(transferCommand)
		transferSourceCommands.forEach { transferCommandLine.addSubcommand(it) }
		transferDestinationCommands.forEach { transferCommandLine.addSubcommand(it) }

		var returnCode = CommandLine(MainCommand())
			.setExecutionStrategy(CommandLine.RunAll())
			.addSubcommand(splitCommand)
			.addSubcommand(combineCommand)
			.addSubcommand(transferCommandLine)
			.execute(*args)

		if (returnCode == 0) {
			if (splitCommand.called) {
				runSplit(splitCommand)
			} else if (combineCommand.called) {
				runCombine(combineCommand)
			} else if (transferCommand.called) {
				returnCode = runTransfer(transferCommand, transferSourceCommands, transferDestinationCommands)
			}
		}

		return returnCode
	}
}