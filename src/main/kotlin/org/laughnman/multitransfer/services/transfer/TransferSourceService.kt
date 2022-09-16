package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.models.transfer.Transfer


interface TransferSourceService {

	fun read(): Flow<Flow<Transfer>>

}