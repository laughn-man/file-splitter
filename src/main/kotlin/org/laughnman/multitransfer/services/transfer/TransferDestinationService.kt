package org.laughnman.multitransfer.services.transfer

import org.laughnman.multitransfer.models.transfer.Transfer


interface TransferDestinationService {

	suspend fun write(): suspend (Transfer) -> Unit

}