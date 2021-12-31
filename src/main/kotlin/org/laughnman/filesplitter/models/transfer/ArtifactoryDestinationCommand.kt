package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.ChunkSize
import org.laughnman.filesplitter.models.ChunkSizeConverter
import org.laughnman.filesplitter.models.ChunkSizeUnit
import picocli.CommandLine.*
import java.net.URI

@Command(name = "dest-artifactory", description = ["Transferring a file to Artifactory."])
class ArtifactoryDestinationCommand : AbstractCommand() {

	class Exclusive {
		@Option(names = ["-p", "--password"], interactive = true, arity = "0..1", description = [
			"The Artifactory user password.",
			"If the value is left blank the password will be requested on STDIN."])
		var password: String = ""

		@Option(names = ["-t", "--token"], interactive = true, arity = "0..1", description = [
			"The Artifactory token.",
			"If the value is left blank the token will be requested on STDIN."])
		var token: String = ""
	}

	@Parameters(description = [
		"The Artifactory URL to deploy to including the repository and any folders.",
		"If url ends in a / then it is assumed that the file will be deployed to a folder."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@ArgGroup(exclusive = true, multiplicity = "1")
	lateinit var exclusive: Exclusive

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}