package org.laughnman.multitransfer.models

import picocli.CommandLine.Option
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "combine", description = ["Combines split files back together."])
class CombineCommand : AbstractCommand() {

	@Parameters(index = "0")
	lateinit var destinationName: Path

	@Parameters(index = "1", arity = "1..*")
	lateinit var paths: Array<Path>

	@Option(names = ["-d", "--delete-chunk"], description = ["Deletes the chunk after it has been copied."])
	var deleteChunk = false

	override fun toString(): String {
		return "CombineCommand(paths=${paths.contentToString()}, destinationName='$destinationName', deleteChunk=$deleteChunk)"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as CombineCommand

		if (destinationName != other.destinationName) return false
		if (!paths.contentEquals(other.paths)) return false
		if (deleteChunk != other.deleteChunk) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + destinationName.hashCode()
		result = 31 * result + paths.contentHashCode()
		result = 31 * result + deleteChunk.hashCode()
		return result
	}


}