package org.laughnman.multitransfer.models.transfer

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "dest-artifactory", description = ["Transferring a file to Artifactory."])
class ArtifactoryDestinationCommand : AbstractArtifactoryCommand() {

	@Parameters(description = ["Destination file path in artifactory. This should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePath: String
}