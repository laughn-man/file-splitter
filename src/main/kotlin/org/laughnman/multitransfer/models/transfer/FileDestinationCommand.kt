package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "dest-file", description = ["Transferring a file to the local file system."])
class FileDestinationCommand : AbstractCommand() {

	@Parameters
	lateinit var path: Path

}