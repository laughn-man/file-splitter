package org.laughnman.multitransfer.models

import picocli.CommandLine.Option
import picocli.CommandLine.Command

@Command(name = "transfer", aliases = ["copy"], subcommandsRepeatable = true, description = ["Copies a source to a destination."])
class TransferCommand : AbstractCommand() {

	@Option(names = ["-p", "--parallelism"], description = ["The number of transfers to perform in parallel."])
	var parallelism = 1

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 10MB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(10, ChunkSizeUnit.MB)
}