package org.laughnman.filesplitter.services

import mu.KotlinLogging
import org.laughnman.filesplitter.models.*
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService
import picocli.CommandLine
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

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

	private fun runTransfer(sourceCommands: Array<out AbstractCommand>, destinationCommands: Array<out AbstractCommand>) {
		logger.debug { "Calling runTransfer sourceCommands: $sourceCommands, destinationCommands: $destinationCommands" }

		val sourceCommand = sourceCommands.filter { it.called }.first()
		val destinationCommand = destinationCommands.filter { it.called }.first()

		val transferSourceService = transferFactoryService.getSourceService(sourceCommand)
		val transferDestinationService = transferFactoryService.getDestinationService(destinationCommand)

		transferSourceService.read().forEach { (metaInfo, sequence) ->
			transferDestinationService.write(metaInfo, sequence)
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
			runTransfer(transferSourceCommands, transferDestinationCommands)
		}
	}
}