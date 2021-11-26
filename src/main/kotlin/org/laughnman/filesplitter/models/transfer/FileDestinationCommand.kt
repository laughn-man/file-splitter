package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.AbstractCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "dest-file", description = ["Transferring a source file."])
class FileDestinationCommand : AbstractCommand() {

	@Parameters
	lateinit var path: Path

}