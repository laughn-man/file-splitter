package org.laughnman.filesplitter.models.transfer

data class TransferInfo(
	val fileName: String,
	val buffer: ByteArray,
	val bytesRead: Int)
{
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as TransferInfo

		if (fileName != other.fileName) return false
		if (!buffer.contentEquals(other.buffer)) return false
		if (bytesRead != other.bytesRead) return false

		return true
	}

	override fun hashCode(): Int {
		var result = fileName.hashCode()
		result = 31 * result + buffer.contentHashCode()
		result = 31 * result + bytesRead
		return result
	}
}

