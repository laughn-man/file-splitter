package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.TransferInfo

interface TransferDestinationService {

	fun write(input: Sequence<TransferInfo>)

}