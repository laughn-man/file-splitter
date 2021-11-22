package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.AbstractCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "src-file", description = ["Transfering a source file."])
class FileSourceCommand : AbstractCommand() {

	@Parameters
	lateinit var filePaths: Array<Path>

}