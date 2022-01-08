package org.laughnman.filesplitter.services

import kotlinx.coroutines.CoroutineScope
import org.laughnman.filesplitter.dao.ArtifactoryDao
import org.laughnman.filesplitter.dao.FileDao
import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.filesplitter.models.transfer.ArtifactorySourceCommand
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.services.transfer.ArtifactoryTransferDestinationServiceImpl
import org.laughnman.filesplitter.services.transfer.ArtifactoryTransferSourceServiceImpl
import org.laughnman.filesplitter.services.transfer.FileTransferDestinationServiceImpl
import org.laughnman.filesplitter.services.transfer.FileTransferSourceServiceImpl
import org.laughnman.filesplitter.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl(private val fileDao: FileDao, private val artifactoryDao: ArtifactoryDao) : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceServiceImpl(command, fileDao)
		is ArtifactorySourceCommand -> ArtifactoryTransferSourceServiceImpl(command, artifactoryDao)
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationServiceImpl(command, fileDao)
		is ArtifactoryDestinationCommand -> ArtifactoryTransferDestinationServiceImpl(command, artifactoryDao)
		else -> throw UnknownCommandException("No source service exists for $command")
	}
}