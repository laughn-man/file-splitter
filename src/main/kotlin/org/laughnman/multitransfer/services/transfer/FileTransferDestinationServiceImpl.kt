package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.FileDestinationCommand
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.TransferInfo
import kotlin.io.path.isDirectory

private val logger = KotlinLogging.logger {}

class FileTransferDestinationServiceImpl(private val command: FileDestinationCommand, private val fileDao: FileDao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<TransferInfo>) {
		logger.debug { "Calling write metaInfo: $metaInfo" }

		val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path

		logger.info { "Opening file $path for writing" }

		withContext(Dispatchers.IO) {
			fileDao.openForWrite(path.toFile()).use { fout ->
				input.collect { transferInfo ->
					logger.trace { "Writing out transferInfo: $transferInfo" }
					fout.write(transferInfo.buffer, 0, transferInfo.bytesRead)
				}
			}
		}
	}
}