package org.laughnman.filesplitter.services.transfer

interface TransferSourceService {

	fun read(f: (bytesRead: Int, buffer: ByteArray) -> Unit)

	fun delete()

}