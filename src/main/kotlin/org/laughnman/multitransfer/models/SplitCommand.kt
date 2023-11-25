package org.laughnman.multitransfer.models

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Path



@Command(name = "split", description = ["Splits up files into chunks."])
class SplitCommand : AbstractCommand() {

	@CommandLine.Parameters
	lateinit var path: Path

	@Option(names = ["-s", "--size"], converter = [ChunkSizeConverter::class],
		description = ["The maximum size of each split file. Format is in <numeric size>B|KB|MB|GB|TB.",
			"Examples:",
			"50: 50 bytes",
			"50B: Also 50 bytes",
			"100KB: 100 Kilobytes",
			"20GB: 20 Gigabytes",
			"5tb: Invalid must be uppercase",
			"5 GB: Invalid cannot have space between size and unit."])
	var chunkSize = ChunkSize(100, ChunkSizeUnit.MB)

	@Option(names = ["-d", "--delete-original"], description = ["Deletes the source file after a successful split."])
	var deleteOriginal = false

	override fun toString(): String {
		return "SplitCommand(path=$path, chunkSize=$chunkSize, deleteOriginal=$deleteOriginal)"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as SplitCommand

		if (path != other.path) return false
		if (chunkSize != other.chunkSize) return false
		if (deleteOriginal != other.deleteOriginal) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + path.hashCode()
		result = 31 * result + chunkSize.hashCode()
		result = 31 * result + deleteOriginal.hashCode()
		return result
	}
}