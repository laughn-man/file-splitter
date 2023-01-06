package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.models.transfer.Transfer
import java.nio.ByteBuffer

typealias SourceReader = (ByteBuffer) -> Flow<Transfer>

interface TransferSourceService {

	fun read(): Flow<SourceReader>

}