package org.laughnman.multitransfer.models.transfer

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "src-artifactory", description = ["Transferring a file from Artifactory."])
class ArtifactorySourceCommand : AbstractArtifactoryCommand() {

	@Parameters(description = ["File paths in artifactory. These should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePaths: Array<String>
}