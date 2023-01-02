package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.flow
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.*
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

class FileTransferSourceServiceImpl(private val command: FileSourceCommand, private val fileDao: FileDao) : TransferSourceService {

	private fun buildFlow(metaInfo: MetaInfo, path: Path): SourceReader = { buffer ->
		flow {
			logger.info { "Reading file $path." }
			emit(Start(metaInfo))

			try {
				fileDao.openReadChannel(path.toFile()).use { channel ->
					while (channel.read(buffer) > 0) {
						emit(Next)
					}
					emit(Complete)
				}
			} catch (e: Exception) {
				emit(Error(e))
			}
		}
	}

	override fun read() = flow {
		command.filePaths.map { path ->
			val metaInfo = MetaInfo(fileName = path.name, fileSize = path.fileSize())
			emit(buildFlow(metaInfo, path))
		}
	}
}