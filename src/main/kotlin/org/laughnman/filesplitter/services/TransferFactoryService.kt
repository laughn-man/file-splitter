package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService

interface TransferFactoryService {

	fun <T: TransferSourceService> getSourceService (command: AbstractCommand): T

	fun <T: TransferDestinationService> getDestinationService (command: AbstractCommand): T

}