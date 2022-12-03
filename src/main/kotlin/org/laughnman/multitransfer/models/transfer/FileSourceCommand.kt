package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeConverter
import org.laughnman.multitransfer.models.ChunkSizeUnit
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import java.nio.file.Path

@Command(name = "src-file", description = ["Transferring a file from the local file system."])
class FileSourceCommand : AbstractCommand() {

	@Parameters(description = ["List of files to be read from.",
		"Shell glob pattern can be used to select multiple files."])
	lateinit var filePaths: Array<Path>

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 5MB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(5, ChunkSizeUnit.MB)

}