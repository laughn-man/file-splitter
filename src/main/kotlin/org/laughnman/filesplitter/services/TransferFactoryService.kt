package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.FunctionalCommand
import org.laughnman.filesplitter.models.transfer.TransferType
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService

interface TransferFactoryService {

	fun <T: TransferSourceService> getSourceService (transferType: TransferType): T

	fun <T: TransferDestinationService> getDestinationService (transferType: TransferType): T

}