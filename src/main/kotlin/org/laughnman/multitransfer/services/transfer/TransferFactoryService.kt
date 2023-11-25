package org.laughnman.multitransfer.services.transfer

import org.laughnman.multitransfer.models.AbstractCommand

interface TransferFactoryService {

	fun getSourceService (command: AbstractCommand): TransferSourceService

	fun getDestinationService (command: AbstractCommand): TransferDestinationService

}