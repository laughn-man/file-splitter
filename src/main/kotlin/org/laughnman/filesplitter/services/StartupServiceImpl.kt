package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.FunctionalCommand
import org.laughnman.filesplitter.models.CombineCommand
import org.laughnman.filesplitter.models.MainCommand
import org.laughnman.filesplitter.models.SplitCommand
import org.slf4j.LoggerFactory
import picocli.CommandLine
import kotlin.system.exitProcess

class StartupServiceImpl(private val fileSplitterService: FileSplitterService) : StartupService {

	private val logger = LoggerFactory.getLogger(this::class.java)

	private fun runSplit(command: FunctionalCommand) {
		logger.debug("Calling runSplit command: $command")
		fileSplitterService.splitFiles(command as SplitCommand)
	}

	private fun runCombine(command: FunctionalCommand) {
		logger.debug("Calling runCombine command: $command")
		fileSplitterService.combineFiles(command as CombineCommand)
	}

	override fun run(args: Array<String>) {
		logger.info("Starting File Splitter.")

		exitProcess(CommandLine(MainCommand(FunctionalCommand::noOp))
			.addSubcommand(SplitCommand(this::runSplit))
			.addSubcommand(CombineCommand(this::runCombine))
			.execute(*args))
	}
}