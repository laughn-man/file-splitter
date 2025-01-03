package org.laughnman.multitransfer.utilities

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

private const val NO_TIME = -1L

private const val BYTE_TO_MEGABYTE_CONVERSION = 1_048_576

private data class TransferRecord(
	val time: Long,
	val bytesTransferred: Int
)

private val logger = KotlinLogging.logger {}

class TransferMonitor(private val fileName: String) {

	private var startTime: Long = NO_TIME
	private var finishTime: Long = NO_TIME

	private var transferTimes = ArrayDeque<TransferRecord>()

	fun start() {
		startTime = System.nanoTime()
	}

	fun stop() {
		finishTime = System.nanoTime()
	}

	fun addTime(bytes: Int) {
		transferTimes.addFirst(TransferRecord(System.nanoTime(), bytes))
	}

	private fun calculateElapsedTime() = if (transferTimes.size == 1) {
			(transferTimes.first().time - startTime).nanoseconds
		} else {
			(transferTimes.first().time - transferTimes[1].time).nanoseconds
		}

	private fun calculateMegaBytesPerSecond(): Double {
		val time = calculateElapsedTime().toDouble(DurationUnit.SECONDS)
		val bytesTransferred = transferTimes.first().bytesTransferred

		return (bytesTransferred / BYTE_TO_MEGABYTE_CONVERSION / time * 100.0).roundToInt() / 100.0
	}

	private fun calculateTotalRunTime() = (finishTime - startTime).nanoseconds

	fun printTransferMessage() {
		val bytesTransferred = (transferTimes.first().bytesTransferred / BYTE_TO_MEGABYTE_CONVERSION.toDouble() * 100.0).roundToInt() / 100.0

		logger.info { "$fileName: Transferred $bytesTransferred MB at ${calculateMegaBytesPerSecond()} MB/s." }
	}

	fun printTotalTransferMessage() {
		val timeStr = calculateTotalRunTime().toComponents { hours, minutes, seconds, nanoseconds ->
			"$hours:$minutes:$seconds.$nanoseconds"
		}

		logger.info { "Transfer job for file $fileName complete, total runtime $timeStr." }
	}
}