package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.Transfer
import org.laughnman.multitransfer.models.transfer.TransferInfo


interface TransferDestinationService {

	suspend fun write(metaInfo: MetaInfo, input: Flow<Transfer>)

}