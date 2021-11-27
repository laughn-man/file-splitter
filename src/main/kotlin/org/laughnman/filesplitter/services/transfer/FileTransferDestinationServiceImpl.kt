package org.laughnman.filesplitter.services.transfer

import mu.KotlinLogging
import org.laughnman.filesplitter.dao.FileDao
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import kotlin.io.path.isDirectory
import kotlin.io.path.outputStream

private val logger = KotlinLogging.logger {}

class FileTransferDestinationServiceImpl(private val command: FileDestinationCommand, private val fileDao: FileDao) : TransferDestinationService {

	override fun write(metaInfo: MetaInfo, input: Sequence<TransferInfo>) {
		logger.debug { "Calling write metaInfo: $metaInfo" }

		val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path

		logger.info { "Opening file $path for writing" }

		fileDao.openForWrite(path.toFile()).use { fout ->
			input.forEach { transferInfo ->
				logger.trace { "Writing out transferInfo: $transferInfo" }
				fout.write(transferInfo.buffer, 0, transferInfo.bytesRead)
			}
		}
	}
}