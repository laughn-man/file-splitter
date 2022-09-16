package org.laughnman.multitransfer.utilities

const val NO_TIME = -1L

class StopWatch {

	private var startTime: Long = NO_TIME
	private var stopTime: Long = NO_TIME
	private var elapsedTime: Long = NO_TIME

	fun start() {
		startTime = System.nanoTime()
	}

	fun stop() {
		stopTime = System.nanoTime()
	}

	fun saveElapsedTime() {
		if (startTime != NO_TIME) {
			elapsedTime = System.nanoTime() - startTime
		}
	}
}