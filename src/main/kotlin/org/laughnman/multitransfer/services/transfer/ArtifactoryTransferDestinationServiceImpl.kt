package org.laughnman.multitransfer.services.transfer

import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.Transfer
import org.laughnman.multitransfer.models.transfer.TransferInfo

private val logger = KotlinLogging.logger {}

class ArtifactoryTransferDestinationServiceImpl(private val command: ArtifactoryDestinationCommand, private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<Transfer>) {
		logger.debug { "Calling write with metaInfo: $metaInfo" }

		// If the file path ends with a slash then it is assumed we are writing to a directory and the file name will need to be added.
		val filePath = if (command.filePath.endsWith("/")) "${command.filePath}${metaInfo.fileName}" else command.filePath

		val buffer = ArrayList<Byte>()

		logger.info { "Buffering ${metaInfo.fileName} for transfer to Artifactory." }
		// Copy each byte array into the buffer.
		// TODO: Find a more optimized way to do this.
		input.collect { transfer ->
			if (transfer is TransferInfo) {
				for (i in 0 until transfer.bytesRead) {
					buffer.add(transfer.buffer[i])
				}
			}
		}

		logger.info { "Deploying file ${metaInfo.fileName} to $filePath" }
		val bufferArr = buffer.toByteArray()

		val fileInfo = try {
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

		logger.info { "File ${metaInfo.fileName} successfully deployed to ${fileInfo.downloadUri}" }
	}
}