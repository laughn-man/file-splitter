package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.FileDestinationCommand
import org.laughnman.multitransfer.models.transfer.Next
import org.laughnman.multitransfer.models.transfer.Start
import org.laughnman.multitransfer.models.transfer.Transfer
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import kotlin.io.path.isDirectory

private val logger = KotlinLogging.logger {}

class FileTransferDestinationServiceImpl(private val command: FileDestinationCommand, private val fileDao: FileDao) : TransferDestinationService {

	override suspend fun write(): suspend (Transfer) -> Unit {

		var fout: OutputStream = ByteArrayOutputStream(0)

		return { transfer ->
			withContext(Dispatchers.IO) {
				when (transfer) {
					is Start -> {
						val metaInfo = transfer.metaInfo
						logger.debug { "Starting file writing metaInfo: $metaInfo" }
						val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path
						fout = fileDao.openForWrite(path.toFile())
					}
					is Next -> {
						logger.trace { "Writing out transferInfo: $transfer" }
						fout.write(transfer.buffer, 0, transfer.bytesRead)
					}
					else -> fout.close()
				}
			}
		}
	}
}