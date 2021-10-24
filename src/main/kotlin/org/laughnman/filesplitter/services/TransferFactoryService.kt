package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.FunctionalCommand
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService

interface TransferFactoryService {

	fun <T: TransferSourceService> getSourceService (command: FunctionalCommand): T

	fun <T: TransferDestinationService> getDestinationService (command: FunctionalCommand): T

}