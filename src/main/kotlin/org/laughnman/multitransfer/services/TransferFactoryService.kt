package org.laughnman.multitransfer.services

import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.services.transfer.TransferDestinationService
import org.laughnman.multitransfer.services.transfer.TransferSourceService

interface TransferFactoryService {

	fun getSourceService (command: AbstractCommand): TransferSourceService

	fun getDestinationService (command: AbstractCommand): TransferDestinationService

}