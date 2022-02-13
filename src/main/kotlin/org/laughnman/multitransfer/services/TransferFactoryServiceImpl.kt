package org.laughnman.multitransfer.services

import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.services.transfer.*
import org.laughnman.multitransfer.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl(private val fileDao: FileDao, private val artifactoryDao: ArtifactoryDao, private val s3DaoFactory: S3Dao.Factory) : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceServiceImpl(command, fileDao)
		is ArtifactorySourceCommand -> ArtifactoryTransferSourceServiceImpl(command, artifactoryDao)
		is S3Command -> S3TransferSourceService(command, s3DaoFactory.fromCommand(command))
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationServiceImpl(command, fileDao)
		is ArtifactoryDestinationCommand -> ArtifactoryTransferDestinationServiceImpl(command, artifactoryDao)
		else -> throw UnknownCommandException("No source service exists for $command")
	}
}