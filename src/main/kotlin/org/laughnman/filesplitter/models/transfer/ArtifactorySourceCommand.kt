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

	@Parameters
	lateinit var filePaths: Array<String>

	@Option(names = ["--url"], required = true, description = ["The Artifactory URL."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@Option(names = ["-p", "--password"], interactive = true, description = ["The Artifactory user password."])
	var password: String = ""

	@Option(names = ["-t", "--token"], interactive = true, description = ["The Artifactory token."])
	var token: String = ""

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}