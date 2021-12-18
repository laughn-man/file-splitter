package org.laughnman.filesplitter.services

import kotlinx.coroutines.CoroutineScope
import org.laughnman.filesplitter.models.AbstractCommand
import org.laughnman.filesplitter.services.transfer.TransferDestinationService
import org.laughnman.filesplitter.services.transfer.TransferSourceService

interface TransferFactoryService {

	fun getSourceService (scope: CoroutineScope, command: AbstractCommand): TransferSourceService

	fun getDestinationService (scope: CoroutineScope, command: AbstractCommand): TransferDestinationService

}