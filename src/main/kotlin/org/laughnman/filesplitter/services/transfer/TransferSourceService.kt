package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo


interface TransferSourceService {

	fun read(): Flow<Pair<MetaInfo, Flow<TransferInfo>>>

}