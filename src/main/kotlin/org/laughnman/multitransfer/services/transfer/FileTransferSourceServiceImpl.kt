package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.transfer.FileSourceCommand
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.TransferInfo
import org.laughnman.multitransfer.utilities.readAsSequence
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

class FileTransferSourceServiceImpl(private val command: FileSourceCommand, private val fileDao: FileDao) : TransferSourceService {

	private fun buildFlow(path: Path): Flow<TransferInfo> = flow {
		logger.debug { "Calling buildSequence path: $path" }

		fileDao.openForRead(path.toFile()).use { fin ->
			fin.readAsSequence(command.bufferSize.toBytes().toInt()).forEach { (readLength, buffer) ->
				emit(TransferInfo(buffer, readLength))
			}
		}
	}.flowOn(Dispatchers.IO)

	override fun read() = flow {
		command.filePaths.map { path ->
			val metaInfo = MetaInfo(fileName = path.name, fileSize = path.fileSize())
			emit(Pair(metaInfo, buildFlow(path)))
		}
	}
}