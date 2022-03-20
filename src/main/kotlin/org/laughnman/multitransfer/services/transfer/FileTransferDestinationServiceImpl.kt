package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.FileDestinationCommand
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.Transfer
import org.laughnman.multitransfer.models.transfer.TransferInfo
import kotlin.io.path.isDirectory

private val logger = KotlinLogging.logger {}

class FileTransferDestinationServiceImpl(private val command: FileDestinationCommand, private val fileDao: FileDao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<Transfer>) {
		logger.debug { "Calling write metaInfo: $metaInfo" }

		val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path

		logger.info { "Opening file $path for writing" }

		withContext(Dispatchers.IO) {
			fileDao.openForWrite(path.toFile()).use { fout ->
				input.collect { transfer ->
					if (transfer is TransferInfo) {
						logger.trace { "Writing out transferInfo: $transfer" }
						fout.write(transfer.buffer, 0, transfer.bytesRead)
					}
				}
			}
		}
	}
}