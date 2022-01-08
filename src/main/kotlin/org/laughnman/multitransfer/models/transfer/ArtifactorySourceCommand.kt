package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeConverter
import org.laughnman.multitransfer.models.ChunkSizeUnit
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import picocli.CommandLine.ArgGroup
import java.net.URI

@Command(name = "src-artifactory", description = ["Transferring a file from Artifactory."])
class ArtifactorySourceCommand : AbstractCommand() {

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

	@Parameters(description = ["File paths in artifactory. These should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePaths: Array<String>

	@Option(names = ["--url"], required = true, description = ["The base Artifactory URL.",
		"Should be the base of the Artifactory URL, not including the repo."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@ArgGroup(exclusive = true, multiplicity = "1")
	lateinit var exclusive: ArtifactoryDestinationCommand.Exclusive

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}