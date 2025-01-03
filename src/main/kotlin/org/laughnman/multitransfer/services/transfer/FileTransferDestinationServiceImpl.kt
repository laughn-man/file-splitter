package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.oshai.kotlinlogging.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.FileDestinationCommand
import org.laughnman.multitransfer.models.transfer.BufferReady
import org.laughnman.multitransfer.models.transfer.Start
import java.nio.channels.WritableByteChannel
import kotlin.io.path.isDirectory

private val logger = KotlinLogging.logger {}

class FileTransferDestinationServiceImpl(private val command: FileDestinationCommand, private val fileDao: FileDao) : TransferDestinationService {

	override suspend fun write(): DestinationWriter {

		lateinit var channel: WritableByteChannel

		return { buffer, transfer ->
			withContext(Dispatchers.IO) {
				when (transfer) {
					is Start -> {
						val metaInfo = transfer.metaInfo
						val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path
						logger.info { "Writing to file $path." }
						channel = fileDao.openWriteChannel(path.toFile())
					}
					is BufferReady -> {
						channel.write(buffer)
					}
					else -> channel.close()
				}
			}
		}
	}
}