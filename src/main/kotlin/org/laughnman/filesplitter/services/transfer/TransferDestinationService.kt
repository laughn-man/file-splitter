package org.laughnman.filesplitter.services.transfer

interface TransferDestinationService {

	fun write(bytesRead: Int, buffer: ByteArray)

	fun delete()

}