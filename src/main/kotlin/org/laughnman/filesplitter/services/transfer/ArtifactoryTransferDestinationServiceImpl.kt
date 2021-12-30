package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.laughnman.filesplitter.dao.ArtifactoryDao
import org.laughnman.filesplitter.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo

class ArtifactoryTransferDestinationServiceImpl(private val command: ArtifactoryDestinationCommand, private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<TransferInfo>) {
		// Copy each byte array into the buffer.
		val buffer = ArrayList<Byte>()
		input.collect { transferInfo ->
			for (i in 0 until transferInfo.bytesRead) {
				buffer.add(transferInfo.buffer[i])
			}
		}

		artifactoryDao.deployArtifact(command.url, buffer.toByteArray(), command.userName, command.password, command.token)
	}
}