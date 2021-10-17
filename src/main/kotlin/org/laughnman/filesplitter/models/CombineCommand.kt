package org.laughnman.filesplitter.models

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "combine", description = ["Combines split files back together."])
class CombineCommand(f: (FunctionalCommand) -> Unit) : FunctionalCommand(f) {

	@Parameters
	lateinit var rootDir: Path

	@Parameters
	lateinit var chunkPattern: String

	@Parameters
	lateinit var destinationName: String

	@CommandLine.Option(names = ["-d", "--delete-chunk"], description = ["Deletes the chunk after it has been copied."])
	var deleteChunk = false

	override fun toString(): String {
		return "CombineCommand(rootDir=$rootDir, chunkPattern='$chunkPattern', destinationName='$destinationName')"
	}
}