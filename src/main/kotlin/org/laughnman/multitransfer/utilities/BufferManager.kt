package org.laughnman.multitransfer.utilities

import org.laughnman.multitransfer.models.transfer.Next
import java.nio.ByteBuffer

class BufferManager(bufferSize: Int) {

	val buffer: ByteBuffer = ByteBuffer.allocate(bufferSize)

	private fun lengthRemaining(startingByte: Int, length: Int) = length - startingByte

	suspend fun put(next: Next, block: suspend (b: ByteBuffer) -> Unit) {
		var startingByte = 0

		while (startingByte < next.bytesRead) {
			if (lengthRemaining(startingByte, next.bytesRead) < buffer.remaining()) {
				buffer.put(next.buffer, startingByte, lengthRemaining(startingByte, next.bytesRead))
				startingByte += lengthRemaining(startingByte, next.bytesRead)
			}
			else {
				val bytesRemaining = buffer.remaining()
				buffer.put(next.buffer, startingByte, bytesRemaining)
				startingByte += bytesRemaining
				block(buffer)
				buffer.clear()
			}
		}
	}
}