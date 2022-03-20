package org.laughnman.multitransfer.utilities

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.laughnman.multitransfer.models.transfer.EOF
import org.laughnman.multitransfer.models.transfer.Transfer
import org.laughnman.multitransfer.models.transfer.TransferInfo
import java.nio.ByteBuffer


fun Flow<TransferInfo>.buffer(bufferSize: Int): Flow<Transfer> {

	var buffer = ByteBuffer.allocate(bufferSize)

	return this.transform<Transfer, Transfer> { t ->
		if (t is TransferInfo) {
			if (t.bytesRead < buffer.remaining()) {
				buffer.put(t.buffer, 0, t.bytesRead)
			}
			else if (t.bytesRead > buffer.remaining()) {
				val bytesLeft = t.bytesRead - buffer.remaining()
				buffer.put(t.buffer, 0, buffer.remaining())
				emit(TransferInfo(buffer))
				buffer = ByteBuffer.allocate(bufferSize)
				buffer.put(t.buffer, bytesLeft, t.bytesRead)
			}
			else {
				buffer.put(t.buffer, 0, buffer.remaining())
				emit(TransferInfo(buffer))
				buffer = ByteBuffer.allocate(bufferSize)
			}
		}
		else {
			emit(TransferInfo(buffer))
			emit(EOF)
		}
	}


}