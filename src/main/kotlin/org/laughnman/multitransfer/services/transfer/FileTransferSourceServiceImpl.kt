package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.utilities.readAsSequence
import java.lang.Exception
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

class FileTransferSourceServiceImpl(private val command: FileSourceCommand, private val fileDao: FileDao) : TransferSourceService {

	private fun buildFlow(metaInfo: MetaInfo, path: Path): Flow<Transfer> = flow {
		logger.info { "Reading file $path." }
		emit(Start(metaInfo))

		try {
			fileDao.openForRead(path.toFile()).use { fin ->
				fin.readAsSequence(command.bufferSize.toBytes().toInt()).forEach { (readLength, buffer) ->
					emit(Next(metaInfo, buffer, readLength))
				}
				emit(Complete(metaInfo))
			}
		}
		catch (e: Exception) {
			emit(Error(metaInfo, e))
		}
	}.flowOn(Dispatchers.IO)

	override fun read() = flow {
		command.filePaths.map { path ->
			val metaInfo = MetaInfo(fileName = path.name, fileSize = path.fileSize())
			emit(buildFlow(metaInfo, path))
		}
	}
}