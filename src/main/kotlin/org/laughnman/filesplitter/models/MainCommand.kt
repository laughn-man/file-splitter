package org.laughnman.filesplitter.models

import picocli.CommandLine.Command

@Command
class MainCommand(f: (FunctionalCommand) -> Unit) : FunctionalCommand(f) {

}