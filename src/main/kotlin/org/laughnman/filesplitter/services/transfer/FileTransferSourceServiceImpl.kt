package org.laughnman.filesplitter.services.transfer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.laughnman.filesplitter.dao.FileDao
import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import org.laughnman.filesplitter.utilities.readAsSequence
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.fileSize
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

class FileTransferSourceServiceImpl(private val command: FileSourceCommand, private val fileDao: FileDao) : TransferSourceService {

	private fun buildChannel(path: Path, scope: CoroutineScope): ReceiveChannel<TransferInfo> {
		logger.debug { "Calling buildSequence path: $path" }

		val channel = Channel<TransferInfo>()

		scope.launch(Dispatchers.IO) {
			fileDao.openForRead(path.toFile()).use { fin ->
				fin.readAsSequence(command.bufferSize.toBytes().toInt()).forEach { (readLength, buffer) ->
					channel.send(TransferInfo(buffer, readLength))
				}
			}
			channel.close()
		}

		return channel
	}

	override fun read(scope: CoroutineScope) = flow {
		command.filePaths.map { path ->
			val metaInfo = MetaInfo(fileName = path.name, fileSize = path.fileSize())
			emit(Pair(metaInfo, buildChannel(path, scope)))
		}
	}
}