package org.laughnman.filesplitter.models

import picocli.CommandLine.Option
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path

@Command(name = "transfer", aliases = ["copy"], subcommandsRepeatable = true, description = ["Copies a source to a destination."])
class TransferCommand : AbstractCommand() {}