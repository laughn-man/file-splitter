package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo

interface TransferSourceService {

	fun read(): List<Pair<MetaInfo, Sequence<TransferInfo>>>

}