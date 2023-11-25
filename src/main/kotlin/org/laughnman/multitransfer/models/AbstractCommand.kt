package org.laughnman.multitransfer.models

import java.util.concurrent.Callable

abstract class AbstractCommand : Callable<Int> {

	var called = false
		private set

	override fun call(): Int {
		called = true
		return 0
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as AbstractCommand

		return called == other.called
	}

	override fun hashCode(): Int {
		return called.hashCode()
	}
}