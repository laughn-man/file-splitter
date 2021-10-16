package org.laughnman.filesplitter.models

import picocli.CommandLine.ITypeConverter
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.nio.file.Path

enum class OperationType {
	SPLIT, COMBINE
}

private class ChunkSizeConverter() : ITypeConverter<ChunkSize> {
	override fun convert(value: String) = value.toChunkSize()
}

class CommandLine {

	@Parameters
	lateinit var operationType: OperationType

	@Parameters
	lateinit var path: Path

	@Option(names = ["-s", "--size"], converter = [ChunkSizeConverter::class], description = ["The size of each split file."])
	var chunkSize = ChunkSize(100, ChunkSizeUnit.MB)
}