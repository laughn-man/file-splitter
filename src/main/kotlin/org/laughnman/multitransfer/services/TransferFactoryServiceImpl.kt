package org.laughnman.multitransfer.services

import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.transfer.ArtifactoryDestinationCommand
import org.laughnman.multitransfer.models.transfer.ArtifactorySourceCommand
import org.laughnman.multitransfer.models.transfer.FileDestinationCommand
import org.laughnman.multitransfer.models.transfer.FileSourceCommand
import org.laughnman.multitransfer.services.transfer.ArtifactoryTransferDestinationServiceImpl
import org.laughnman.multitransfer.services.transfer.ArtifactoryTransferSourceServiceImpl
import org.laughnman.multitransfer.services.transfer.FileTransferDestinationServiceImpl
import org.laughnman.multitransfer.services.transfer.FileTransferSourceServiceImpl
import org.laughnman.multitransfer.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl(private val fileDao: FileDao, private val artifactoryDaoFactory: ArtifactoryDao.Factory) : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceServiceImpl(command, fileDao)
		is ArtifactorySourceCommand -> ArtifactoryTransferSourceServiceImpl(command, artifactoryDaoFactory.fromCommand(command))
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationServiceImpl(command, fileDao)
		is ArtifactoryDestinationCommand -> ArtifactoryTransferDestinationServiceImpl(command, artifactoryDaoFactory.fromCommand(command))
		else -> throw UnknownCommandException("No source service exists for $command")
	}
}