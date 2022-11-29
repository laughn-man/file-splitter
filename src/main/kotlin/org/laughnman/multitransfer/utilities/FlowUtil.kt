package org.laughnman.multitransfer.utilities

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.laughnman.multitransfer.models.transfer.Next
import org.laughnman.multitransfer.models.transfer.Start
import org.laughnman.multitransfer.models.transfer.Transfer
import java.nio.ByteBuffer


fun Flow<Next>.buffer(bufferSize: Int): Flow<Transfer> {

	lateinit var buffer: ByteBuffer

	return this.transform<Transfer, Transfer> { t ->
		when(t) {
			is Start -> {
				buffer = ByteBuffer.allocate(bufferSize)
				emit(t)
			}
			is Next -> {
				if (t.bytesRead < buffer.remaining()) {
					buffer.put(t.buffer, 0, t.bytesRead)
				}
				else if (t.bytesRead > buffer.remaining()) {
					val bytesLeft = t.bytesRead - buffer.remaining()
					buffer.put(t.buffer, 0, buffer.remaining())
					emit(Next(t.metaInfo, buffer))
					buffer = ByteBuffer.allocate(bufferSize)
					buffer.put(t.buffer, bytesLeft, t.bytesRead)
				}
				else {
					buffer.put(t.buffer, 0, buffer.remaining())
					emit(Next(t.metaInfo, buffer))
					buffer = ByteBuffer.allocate(bufferSize)
				}
			}
			else -> {
				emit(Next(t.metaInfo, buffer))
				emit(t)
			}
		}
	}
}