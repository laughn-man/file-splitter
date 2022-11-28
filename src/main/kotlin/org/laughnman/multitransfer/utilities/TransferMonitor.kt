package org.laughnman.multitransfer.utilities

import kotlin.time.Duration.Companion.nanoseconds

private const val NO_TIME = -1L

private const val BYTE_TO_MEGABYTE_CONVERSION = 1_000_000

private data class TransferRecord(
	val time: Long,
	val bytesTransferred: Int
)


class TransferMonitor {

	private var startTime: Long = NO_TIME
	private var finishTime: Long = NO_TIME

	private var transferRecords: MutableList<TransferRecord> = ArrayList()

	fun start() {
		startTime = System.nanoTime()
	}

	fun stop() {
		finishTime = System.nanoTime()
	}

	fun addTransferRecord(bytesTransferred: Int) {
		transferRecords.add(TransferRecord(System.nanoTime(), bytesTransferred))
	}

	fun calculateElapsedTime() = if (transferRecords.size == 1) {
		(transferRecords.last().time - startTime).nanoseconds
	}
	else {
		(transferRecords.last().time - transferRecords[transferRecords.size - 2].time).nanoseconds
	}

	fun calculateMegaBytesPerSecond(): Double {
		val time = calculateElapsedTime().inWholeSeconds
		val bytesTransferred = transferRecords.last().bytesTransferred

		return (bytesTransferred / BYTE_TO_MEGABYTE_CONVERSION) / time.toDouble()
	}

	fun calculateTotalRunTime() = (finishTime - startTime).nanoseconds

	fun calculateTotalMegaBytesPerSecond(): Double {
		val time = calculateTotalRunTime().inWholeSeconds
		val bytesTransferred = transferRecords.sumOf { it.bytesTransferred.toLong() }

		return (bytesTransferred / BYTE_TO_MEGABYTE_CONVERSION) / time.toDouble()
	}
}