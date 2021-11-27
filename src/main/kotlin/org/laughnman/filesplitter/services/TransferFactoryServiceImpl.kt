package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.dao.FileDao
import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.services.transfer.FileTransferDestinationServiceImpl
import org.laughnman.filesplitter.services.transfer.FileTransferSourceServiceImpl
import org.laughnman.filesplitter.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl(private val fileDao: FileDao) : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceServiceImpl(command, fileDao)
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationServiceImpl(command, fileDao)
		else -> throw UnknownCommandException("No source service exists for $command")
	}
}