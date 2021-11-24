package org.laughnman.filesplitter.models.transfer

data class MetaInfo(
	val fileName: String,
	val fileSize: Long = -1L
)
