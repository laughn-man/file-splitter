package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.TransferCommand
import org.laughnman.multitransfer.models.transfer.BufferReady
import org.laughnman.multitransfer.models.transfer.Complete
import org.laughnman.multitransfer.models.transfer.Error
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.Start
import org.laughnman.multitransfer.utilities.TransferMonitor
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

private const val SLEEP_TIME = 100L

class TransferServiceImpl(private val transferFactoryService: TransferFactoryService) : TransferService {

	private suspend fun buildJob(bufferSize: ChunkSize, transferDestinationService: TransferDestinationService,
	                             sourceReader: SourceReader) {
		lateinit var metaInfo: MetaInfo
		lateinit var transferMonitor: TransferMonitor

		logger.info { Thread.currentThread().name }

		val destinationWriter = transferDestinationService.write()
		val buffer = ByteBuffer.allocateDirect(bufferSize.toBytes().toInt())

		sourceReader(buffer).collect { transfer ->
			when (transfer) {
				is Start -> {
					metaInfo = transfer.metaInfo
					transferMonitor = TransferMonitor(metaInfo.fileName)
					transferMonitor.start()
					logger.info { "${metaInfo.fileName}: Starting transfer job." }
					destinationWriter(buffer, transfer)
				}
				is BufferReady -> {
					val bytesRead = buffer.position()
					// Reset the buffer so it is ready for reading.
					buffer.flip()
					destinationWriter(buffer, transfer)
					transferMonitor.addTime(bytesRead)
					// Clear out the buffer so it is ready to be written to again.
					buffer.clear()
					transferMonitor.printTransferMessage()
				}
				is Complete -> {
					destinationWriter(buffer, transfer)
					transferMonitor.stop()
					logger.info { "${metaInfo.fileName}: Finishing transfer job." }
				}
				is Error -> {
					destinationWriter(buffer, transfer)
					transferMonitor.stop()
					logger.error(transfer.exception) { "${metaInfo.fileName}: Exception occurred in transfer job." }
				}
			}
		}

		transferMonitor.printTotalTransferMessage()
	}

	/**
	 * Checks to see if the processBuffer is full, if so it delays until at least 1 job has finished.
	 */
	private suspend fun waitForProcess(processBuffer: MutableList<Job>, maxProcesses: Int) {
		while (processBuffer.size == maxProcesses) {
			// Wait for a bit to see if a job frees up.
			delay(SLEEP_TIME)

			// Loop backwards so items can be removed from the list without causing issues.
			for (i in processBuffer.indices.reversed()) {
				val job = processBuffer[i]
				if (job.isCompleted) {
					processBuffer.removeAt(i)
				}
			}
		}
	}

	override fun runTransfer(transferCommand: TransferCommand, sourceCommand: AbstractCommand, destinationCommand: AbstractCommand) {
		logger.debug { "Calling runTransfer sourceCommands: $sourceCommand, destinationCommands: $destinationCommand" }

		val processBuffer = ArrayList<Job>(transferCommand.parallelism)

		runBlocking {
			val transferSourceService = transferFactoryService.getSourceService(sourceCommand)
			val transferDestinationService = transferFactoryService.getDestinationService(destinationCommand)

			transferSourceService.read().collect { sourceReader ->
				// Wait if the process buffer is full.
				waitForProcess(processBuffer, transferCommand.parallelism)
				// Launch a new process.
				processBuffer += launch(Dispatchers.IO) {
					buildJob(transferCommand.bufferSize, transferDestinationService, sourceReader)
				}
			}

			// Wait on the remaining jobs.
			for (job in processBuffer) {
				job.join()
			}
		}
	}
}