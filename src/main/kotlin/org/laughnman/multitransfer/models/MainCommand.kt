package org.laughnman.multitransfer.models

import org.laughnman.multitransfer.utilities.VersionProvider
import picocli.CommandLine.Option
import picocli.CommandLine.Command

@Command(name = "multi-transfer", versionProvider = VersionProvider::class)
class MainCommand : AbstractCommand() {

	@Option(names = ["-V", "--version"], versionHelp = true, description = ["Display version info."])
	var versionInfoRequested = false

	@Option(names = ["-h", "--help"], usageHelp = true, description = ["Display this help message."])
	var usageHelpRequested = false

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as MainCommand

		if (versionInfoRequested != other.versionInfoRequested) return false
		if (usageHelpRequested != other.usageHelpRequested) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + versionInfoRequested.hashCode()
		result = 31 * result + usageHelpRequested.hashCode()
		return result
	}


}