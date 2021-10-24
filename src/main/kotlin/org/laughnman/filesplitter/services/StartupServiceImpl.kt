package org.laughnman.filesplitter.services

import mu.KotlinLogging
import org.laughnman.filesplitter.models.FunctionalCommand
import org.laughnman.filesplitter.models.CombineCommand
import org.laughnman.filesplitter.models.MainCommand
import org.laughnman.filesplitter.models.SplitCommand
import picocli.CommandLine
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class StartupServiceImpl(private val fileSplitterService: FileSplitterService) : StartupService {

	private fun runSplit(command: FunctionalCommand) {
		logger.debug { "Calling runSplit command: $command" }
		fileSplitterService.splitFiles(command as SplitCommand)
	}

	private fun runCombine(command: FunctionalCommand) {
		logger.debug { "Calling runCombine command: $command" }
		fileSplitterService.combineFiles(command as CombineCommand)
	}

	override fun run(args: Array<String>) {
		logger.info { "Starting Universal Transfer." }

		exitProcess(CommandLine(MainCommand())
			.addSubcommand(SplitCommand(this::runSplit))
			.addSubcommand(CombineCommand(this::runCombine))
			.execute(*args))
	}
}