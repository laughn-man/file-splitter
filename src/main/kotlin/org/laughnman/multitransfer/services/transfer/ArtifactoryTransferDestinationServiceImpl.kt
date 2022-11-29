package org.laughnman.multitransfer.services.transfer

import io.ktor.client.features.*
import io.ktor.http.*
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.models.transfer.*
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

class ArtifactoryTransferDestinationServiceImpl(private val command: ArtifactoryDestinationCommand, private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override suspend fun write(): suspend (Transfer) -> Unit {

		lateinit var buffer: ByteBuffer

		return { transfer ->
			when(transfer) {
				is Start -> {
					logger.debug { "Calling write with metaInfo: ${transfer.metaInfo}" }
					buffer = ByteBuffer.allocate(transfer.metaInfo.fileSize.toInt())
				}
				is Next -> {
					buffer.put(transfer.buffer, 0, transfer.bytesRead)
				}
				is Complete -> {
					val filePath = if (command.filePath.endsWith("/")) "${command.filePath}${transfer.metaInfo.fileName}" else command.filePath
					val bufferArr = buffer.array()
					try {
						artifactoryDao.deployArtifactWithChecksum(command.url, filePath, bufferArr, command.userName, command.exclusive.password, command.exclusive.token)
					}
					catch (e: ClientRequestException) {
						if (e.response.status == HttpStatusCode.NotFound) {
							logger.info { "Cached file not found for ${transfer.metaInfo.fileName}, uploading new version." }
							artifactoryDao.deployArtifact(command.url, filePath, bufferArr, command.userName, command.exclusive.password, command.exclusive.token)
						}
						else {
							throw e
						}
					}
				}
				is Error -> {
					logger.error(transfer.exception) { "Error received from source." }
				}
			}
		}
	}
}