package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "dest-file", description = ["Transferring a file to the local file system."])
class FileDestinationCommand : AbstractCommand() {

	@Parameters(description = ["Path to transfer files into.",
		"If the path is a directory the files will be placed inside of it."])
	lateinit var path: Path

}