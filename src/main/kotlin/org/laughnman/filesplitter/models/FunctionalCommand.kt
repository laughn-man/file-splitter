package org.laughnman.filesplitter.models

import java.util.concurrent.Callable



open class FunctionalCommand(private val f: (command: FunctionalCommand) -> Unit) : Callable<Int> {

	companion object {
		fun noOp(command: FunctionalCommand) { }
	}

	override fun call(): Int {
		f(this)
		return 0
	}
}