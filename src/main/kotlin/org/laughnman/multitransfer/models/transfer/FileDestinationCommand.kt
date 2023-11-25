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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as FileDestinationCommand

		return path == other.path
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + path.hashCode()
		return result
	}
}