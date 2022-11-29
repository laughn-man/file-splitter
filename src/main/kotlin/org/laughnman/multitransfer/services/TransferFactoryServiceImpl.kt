package org.laughnman.multitransfer.services

import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.*
import org.laughnman.multitransfer.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl(private val fileDao: FileDao, private val s3DaoFactory: S3Dao.Factory,
																 private val artifactoryDaoFactory: ArtifactoryDao.Factory) : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceServiceImpl(command, fileDao)
		is ArtifactorySourceCommand -> ArtifactoryTransferSourceServiceImpl(command, artifactoryDaoFactory.fromCommand(command))
		is S3SourceCommand -> S3TransferSourceServiceImpl(command, s3DaoFactory.fromCommand(command))
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationServiceImpl(command, fileDao)
		is ArtifactoryDestinationCommand -> ArtifactoryTransferDestinationServiceImpl(command, artifactoryDaoFactory.fromCommand(command))
		is S3DestinationCommand -> S3TransferDestinationServiceImpl(command, s3DaoFactory.fromCommand(command))
		else -> throw UnknownCommandException("No destination service exists for $command")
	}
}