package org.laughnman.filesplitter.models

import java.util.concurrent.Callable

abstract class AbstractCommand : Callable<Int> {

	var called = false
		private set

	override fun call(): Int {
		called = true
		return 0
	}
}