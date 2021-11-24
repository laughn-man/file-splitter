package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.services.transfer.FileTransferDestinationService
import org.laughnman.filesplitter.services.transfer.FileTransferSourceService
import org.laughnman.filesplitter.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl : TransferFactoryService {

	override fun getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileTransferSourceService(command)
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun getDestinationService(command: AbstractCommand) = when(command) {
		is FileDestinationCommand -> FileTransferDestinationService(command)
		else -> throw UnknownCommandException("No source service exists for $command")
	}
}