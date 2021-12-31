package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.ChunkSize
import org.laughnman.filesplitter.models.ChunkSizeConverter
import org.laughnman.filesplitter.models.ChunkSizeUnit
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import java.net.URI
import java.nio.file.Path

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

	@Parameters(arity = "0", description = ["File paths in artifactory. These will be appended on to the --base-url.",
		"If not provided then the --base-url will be used the single source.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred.",])
	lateinit var filePaths: Array<String>

	@Option(names = ["--base-url"], required = true, description = ["The base Artifactory URL.",
		"Should be the base of the Artifactory URL down the lowest common path for all the filePaths."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}