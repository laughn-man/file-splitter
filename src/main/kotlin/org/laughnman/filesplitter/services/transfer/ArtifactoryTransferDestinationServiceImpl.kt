package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.laughnman.filesplitter.dao.ArtifactoryDao
import org.laughnman.filesplitter.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.net.URI

private val logger = KotlinLogging.logger {}

class ArtifactoryTransferDestinationServiceImpl(private val command: ArtifactoryDestinationCommand, private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<TransferInfo>) {
		logger.debug { "Calling write with metaInfo: $metaInfo" }

		// If the url ends with a slash then it is assumed we are writing to a directory and the file name will need to be added.
		val url = if (command.url.path.endsWith("/")) URI("${command.url}${metaInfo.fileName}") else command.url

		val buffer = ArrayList<Byte>()

		logger.info { "Buffering ${metaInfo.fileName} for transfer to Artifactory." }
		// Copy each byte array into the buffer.
		// TODO: Find a more optimized way to do this.
		input.collect { transferInfo ->
			for (i in 0 until transferInfo.bytesRead) {
				buffer.add(transferInfo.buffer[i])
			}
		}

		logger.info { "Deploying file ${metaInfo.fileName} to $url" }

		// Move the file into artifactory.
		val fileInfo = artifactoryDao.deployArtifact(url, buffer.toByteArray(), command.userName, command.exclusive.password, command.exclusive.token)

		logger.info { "File ${metaInfo.fileName} successfully deployed ${fileInfo.downloadUri}" }
	}
}