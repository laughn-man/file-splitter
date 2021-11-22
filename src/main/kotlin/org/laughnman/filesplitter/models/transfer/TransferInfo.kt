package org.laughnman.filesplitter.models.transfer

data class TransferInfo(
	val fileName: String,
	val buffer: ByteArray,
	val bufferLength: Int,
	val first: Boolean = false,
	val last: Boolean = false)
{
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as TransferInfo

		if (fileName != other.fileName) return false
		if (!buffer.contentEquals(other.buffer)) return false
		if (bufferLength != other.bufferLength) return false
		if (first != other.first) return false
		if (last != other.last) return false

		return true
	}

	override fun hashCode(): Int {
		var result = fileName.hashCode()
		result = 31 * result + buffer.contentHashCode()
		result = 31 * result + bufferLength
		result = 31 * result + first.hashCode()
		result = 31 * result + last.hashCode()
		return result
	}
}

