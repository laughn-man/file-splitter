package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import org.laughnman.filesplitter.dao.ArtifactoryDao
import org.laughnman.filesplitter.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo

class ArtifactoryTransferDestinationServiceImpl(private val scope: CoroutineScope,
	private val command: ArtifactoryDestinationCommand,
	private val artifactoryDao: ArtifactoryDao) : TransferDestinationService {

	override fun write(metaInfo: MetaInfo, input: ReceiveChannel<TransferInfo>) {
		TODO("Not yet implemented")
	}
}