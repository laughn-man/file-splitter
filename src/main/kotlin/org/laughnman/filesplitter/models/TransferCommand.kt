package org.laughnman.filesplitter.models

import picocli.CommandLine.Option
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "transfer", aliases = ["copy"], description = ["Copies a source to a destination."])
class TransferCommand(f: (FunctionalCommand) -> Unit) : FunctionalCommand(f) {

	@Parameters
	lateinit var source: String

	@Parameters
	lateinit var destination: String

	@Option(names = ["-d", "--delete"], description = ["Deletes the source after it has been successfully transferred."])
	var deleteSource = false

}