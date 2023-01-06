package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.laughnman.multitransfer.dao.ArtifactoryDao
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.utilities.findFileName

private const val SUSPEND_TIME = 100L

class ArtifactoryTransferSourceServiceImpl(private val command: ArtifactorySourceCommand,	private val artifactoryDao: ArtifactoryDao) : TransferSourceService {

	private suspend fun buildMetaInfo(filePath: String): MetaInfo {
		val fileInfo = artifactoryDao.getFileInfo(command.url, filePath, command.userName, command.exclusive.password, command.exclusive.token)
		return MetaInfo(fileName = filePath.findFileName(), fileSize = fileInfo.size)
	}

	private suspend fun buildFlow(metaInfo: MetaInfo, filePath: String): SourceReader = { buffer ->
		flow {
			artifactoryDao.downloadArtifact(command.url, filePath, command.userName, command.exclusive.password, command.exclusive.token) { channel ->
				emit(Start(metaInfo))

				try {
					while (!channel.isClosedForRead) {
						// Read all available bytes into the buffer.
						val bytesRead = channel.readAvailable(buffer)
						// If the buffer is full emit it for reading.
						if (!buffer.hasRemaining()) {
							emit(BufferReady)
						}
						// If no bytes are read then suspend for a little bit.
						if (bytesRead == 0) {
							delay(SUSPEND_TIME)
						}
					}

					// If there is anything left in the buffer do one last BufferReady call.
					if (buffer.position() > 0) {
						emit(BufferReady)
					}

					emit(Complete)
				}
				catch (e: Exception) {
					emit(Error(e))
				}
			}
		}
	}

	override fun read() = flow {
		command.filePaths.map { path ->
			// If the path is a folder then get the folder children and emit for each file. Else just emit the file as normal.
			if (path.endsWith("/")) {
				// Get all the files in the folder.
				val folderInfo = artifactoryDao.getFolderInfo(command.url, path, command.userName, command.exclusive.password, command.exclusive.token)
				folderInfo.children
					.filter { !it.folder }
					.forEach { child ->
						val filePath = "${folderInfo.repo}/${folderInfo.path}${child.uri}"
						val metaInfo = buildMetaInfo(filePath)
						emit(buildFlow(metaInfo, filePath))
					}
			}
			else {
				val metaInfo = buildMetaInfo(path)
				emit(buildFlow(metaInfo, path))
			}
		}
	}
}