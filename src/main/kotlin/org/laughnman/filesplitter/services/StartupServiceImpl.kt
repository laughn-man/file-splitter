package org.laughnman.filesplitter.services

import mu.KotlinLogging
import org.laughnman.filesplitter.models.*
import org.laughnman.filesplitter.models.transfer.TransferParameters
import org.laughnman.filesplitter.utilities.Direction.SOURCE
import org.laughnman.filesplitter.utilities.Direction.DESTINATION
import org.laughnman.filesplitter.utilities.TransferParametersParser
import picocli.CommandLine
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class StartupServiceImpl(private val fileSplitterService: FileSplitterService,
	private val transferParametersParser: TransferParametersParser
) : StartupService {

	private fun runSplit(command: FunctionalCommand) {
		logger.debug { "Calling runSplit command: $command" }
		fileSplitterService.splitFiles(command as SplitCommand)
	}

	private fun runCombine(command: FunctionalCommand) {
		logger.debug { "Calling runCombine command: $command" }
		fileSplitterService.combineFiles(command as CombineCommand)
	}

	private fun runTransfer(command: FunctionalCommand) {
		logger.debug { "Calling runTransfer command: $command" }
		val transferCommand = command as TransferCommand
		val sourceParameters: TransferParameters = transferParametersParser.parse(SOURCE, transferCommand.source)
		val destinationParameters: TransferParameters = transferParametersParser.parse(DESTINATION, transferCommand.destination)


	}

	override fun run(args: Array<String>) {
		logger.info { "Starting Universal Transfer." }

		exitProcess(CommandLine(MainCommand())
			.addSubcommand(SplitCommand(this::runSplit))
			.addSubcommand(CombineCommand(this::runCombine))
			.addSubcommand(TransferCommand(this::runTransfer))
			.execute(*args))
	}
}