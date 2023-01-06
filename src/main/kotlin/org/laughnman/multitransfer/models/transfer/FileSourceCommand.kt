package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "src-file", description = ["Transferring a file from the local file system."])
class FileSourceCommand : AbstractCommand() {

	@Parameters(description = ["List of files to be read from.",
		"Shell glob pattern can be used to select multiple files."])
	lateinit var filePaths: Array<Path>
}