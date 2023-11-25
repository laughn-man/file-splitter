package org.laughnman.multitransfer.models.transfer

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "dest-artifactory", description = ["Transferring a file to Artifactory."])
class ArtifactoryDestinationCommand : AbstractArtifactoryCommand() {

	@Parameters(description = ["Destination file path in artifactory. This should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePath: String

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as ArtifactoryDestinationCommand

		return filePath == other.filePath
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + filePath.hashCode()
		return result
	}
}