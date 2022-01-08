package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.TransferInfo


interface TransferSourceService {

	fun read(): Flow<Pair<MetaInfo, Flow<TransferInfo>>>

}