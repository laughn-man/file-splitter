package org.laughnman.multitransfer.models.transfer

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "src-artifactory", description = ["Transferring a file from Artifactory."])
class ArtifactorySourceCommand : AbstractArtifactoryCommand() {

	@Parameters(description = ["File paths in artifactory. These should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePaths: Array<String>

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as ArtifactorySourceCommand

		return filePaths.contentEquals(other.filePaths)
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + filePaths.contentHashCode()
		return result
	}
}