package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo

interface TransferDestinationService {

	fun write(metaInfo: MetaInfo, input: Sequence<TransferInfo>)

}