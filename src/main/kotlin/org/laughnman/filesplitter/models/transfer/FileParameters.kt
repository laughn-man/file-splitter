package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.transfer.TransferType.FILE

data class FileSourceParameters(
	override val type: TransferType = FILE,
	val path: String
) : TransferParameters

data class FileDestinationParameters(
	override val type: TransferType = FILE,
	val path: String
) : TransferParameters