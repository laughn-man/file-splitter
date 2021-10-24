package org.laughnman.filesplitter.models.transfer

data class FileSourceParameters(
	override val type: TransferType,
	val path: String
) : TransferParameters

data class FileDestinationParameters(
	override val type: TransferType,
	val path: String
) : TransferParameters