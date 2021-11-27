package org.laughnman.filesplitter.services.transfer

import mu.KotlinLogging
import org.laughnman.filesplitter.dao.FileDao
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

class FileTransferSourceServiceImpl(private val command: FileSourceCommand, private val fileDao: FileDao) : TransferSourceService {

	private fun buildSequence(path: Path): Sequence<TransferInfo> {
		logger.debug { "Calling buildSequence path: $path" }

		var fin: InputStream? = null
		val buffer = ByteArray(command.bufferSize.toBytes().toInt())

		return generateSequence {
			if (fin == null) {
				logger.info { "Reading file $path" }
				fin = fileDao.openForRead(path.toFile())
			}

			try {
				val bytesRead = fin!!.read(buffer, 0, buffer.size)

				logger.trace { "bytesRead: $bytesRead" }

				if (bytesRead == -1) {
					logger.debug { "Closing file $path" }
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