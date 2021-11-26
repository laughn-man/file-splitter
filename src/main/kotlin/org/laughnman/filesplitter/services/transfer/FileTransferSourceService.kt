package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.io.FileInputStream
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

class FileTransferSourceService(private val command: FileSourceCommand) : TransferSourceService {

	private fun buildSequence(path: Path): Sequence<TransferInfo> {
		var fin: FileInputStream? = null
		val buffer = ByteArray(command.bufferSize.toBytes().toInt())

		return generateSequence {
			if (fin == null) {
				fin = path.toFile().inputStream()
			}

			try {
				val bytesRead = fin!!.read(buffer, 0, buffer.size)

				if (bytesRead == -1) {
					fin!!.close()
					null
				}
				else {
					TransferInfo(fileName = path.name, buffer = buffer, bytesRead = bytesRead)
				}
			}
			catch (e: Exception) {
				fin!!.close()
				throw e
			}
		}
	}

	override fun read() = command.filePaths.map { path ->
		val metaInfo = MetaInfo(fileName = path.name, fileSize = path.fileSize())
		val sequence = buildSequence(path)
		Pair(metaInfo, sequence)
	}
}