package org.laughnman.multitransfer.services.transfer

import org.laughnman.multitransfer.models.transfer.Transfer
import java.nio.ByteBuffer

typealias DestinationWriter = suspend (buffer: ByteBuffer, Transfer) -> Unit
interface TransferDestinationService {

	suspend fun write(): DestinationWriter

}