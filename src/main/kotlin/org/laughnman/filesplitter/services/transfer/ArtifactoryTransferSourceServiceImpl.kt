package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import org.laughnman.filesplitter.dao.ArtifactoryDao
import org.laughnman.filesplitter.models.transfer.ArtifactorySourceCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo

class ArtifactoryTransferSourceServiceImpl(private val command: ArtifactorySourceCommand,	private val artifactoryDao: ArtifactoryDao) : TransferSourceService {

	override fun read(): Flow<Pair<MetaInfo, Flow<TransferInfo>>> {
		TODO("Not yet implemented")
	}
}