package org.laughnman.multitransfer.models.transfer

import java.lang.Exception
import java.nio.ByteBuffer

sealed interface Transfer {
	val metaInfo: MetaInfo
}

data class Complete(override val metaInfo: MetaInfo) : Transfer

data class Start(override val metaInfo: MetaInfo) : Transfer

data class Error(override val metaInfo: MetaInfo, val exception: Exception) : Transfer

data class Next(
	override val metaInfo: MetaInfo,
	val buffer: ByteArray,
	val bytesRead: Int) : Transfer
{
	constructor(metaInfo: MetaInfo, b: ByteBuffer) : this(metaInfo, b.array(), b.position() + 1)

	fun toByteBuffer(): ByteBuffer {
		val byteBuffer = ByteBuffer.allocate(bytesRead)
		byteBuffer.put(buffer, 0, bytesRead)
		return byteBuffer
	}

	fun minSizeBuffer(): ByteArray = buffer.copyOf(bytesRead)
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Next

		if (metaInfo != other.metaInfo) return false
		if (!buffer.contentEquals(other.buffer)) return false
		if (bytesRead != other.bytesRead) return false

		return true
	}

	override fun hashCode(): Int {
		var result = metaInfo.hashCode()
		result = 31 * result + buffer.contentHashCode()
		result = 31 * result + bytesRead
		return result
	}


}

