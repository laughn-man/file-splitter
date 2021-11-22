package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService
import org.laughnman.filesplitter.utilities.exceptions.UnknownCommandException

class TransferFactoryServiceImpl : TransferFactoryService {

	override fun <T : TransferSourceService> getSourceService(command: AbstractCommand) = when(command) {
		is FileSourceCommand -> FileSourceCommand()
		else -> throw UnknownCommandException("No source service exists for $command")
	}

	override fun <T : TransferDestinationService> getDestinationService(command: AbstractCommand): T {
		TODO("Not yet implemented")
	}
}