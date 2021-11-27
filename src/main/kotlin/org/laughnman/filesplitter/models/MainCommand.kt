package org.laughnman.filesplitter.models

import org.laughnman.filesplitter.utilities.VersionProvider
import picocli.CommandLine.Option
import picocli.CommandLine.Command

@Command(name = "universal-transfer", versionProvider = VersionProvider::class)
class MainCommand : AbstractCommand() {

	@Option(names = ["-V", "--version"], versionHelp = true, description = ["Display version info."])
	var versionInfoRequested = false

	@Option(names = ["-h", "--help"], usageHelp = true, description = ["Display this help message."])
	var usageHelpRequested = false

}