package org.laughnman.multitransfer.services.transfer

import io.ktor.client.plugins.*
import io.ktor.http.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.models.transfer.*
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

class ArtifactoryTransferDestinationServiceImpl(private val command: ArtifactoryDestinationCommand, private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override suspend fun write(): DestinationWriter {

		lateinit var transferBuffer: ByteBuffer
		lateinit var metaInfo: MetaInfo

		return { buffer, transfer ->
			when(transfer) {
				is Start -> {
					metaInfo = transfer.metaInfo
					transferBuffer = ByteBuffer.allocate(transfer.metaInfo.fileSize.toInt())
				}
				is BufferReady -> {
					transferBuffer.put(buffer)
				}
				is Complete -> {
					val filePath = if (command.filePath.endsWith("/")) "${command.filePath}${metaInfo.fileName}" else command.filePath
					val bufferArr = transferBuffer.array()
					try {
						artifactoryDao.deployArtifactWithChecksum(command.url, filePath, bufferArr, command.userName, command.exclusive.password, command.exclusive.token)
					}
					catch (e: ClientRequestException) {
						if (e.response.status == HttpStatusCode.NotFound) {
							logger.info { "Cached file not found for ${metaInfo.fileName}, uploading new version." }
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