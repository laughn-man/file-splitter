package org.laughnman.multitransfer.services

import mu.KotlinLogging
import org.laughnman.multitransfer.dao.FileDao
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.CombineCommand
import org.laughnman.multitransfer.models.SplitCommand
import org.laughnman.multitransfer.utilities.exceptions.InvalidFileSpecificationsException
import org.laughnman.multitransfer.utilities.sha256Hash
import org.laughnman.multitransfer.utilities.toHex
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.min

private const val BUFFER_SIZE: Int = 4_096
private const val INDEX_PADDING: Int = 6

private val logger = KotlinLogging.logger {}

class FileSplitterServiceImpl(private val fileDao: FileDao) : FileSplitterService {

	private fun copyFileChunk(fin: InputStream, fout: OutputStream, chunkSize: ChunkSize): Long {
		val buffer = ByteArray(BUFFER_SIZE)
		val maxBytes = chunkSize.toBytes()
		val maxRead = min(maxBytes.toInt(), BUFFER_SIZE)
		var bytesCopied = 0L

		while (bytesCopied < maxBytes) {
			val length = fin.read(buffer, 0, maxRead)

			logger.debug { "length: $length" }

			if (length == -1) {
				break
			}

			fout.write(buffer, 0, length)
			bytesCopied += length
		}

		return bytesCopied
	}

	override fun splitFiles(splitCommand: SplitCommand) {
		logger.debug { "Calling splitFiles splitCommand: $splitCommand" }

		val file = splitCommand.path.toFile()

		if (!fileDao.isFile(file)) {
			throw InvalidFileSpecificationsException("$file must be a file.")
		}
		if (fileDao.getLength(file) <= splitCommand.chunkSize.toBytes()) {
			throw InvalidFileSpecificationsException("$file length of ${file.length()} is less than or equal to chunk size. Nothing to split.")
		}

		logger.info {"SHA-256 Hash: ${fileDao.openForRead(file).use { it.sha256Hash().toHex()	}}"}

		// Open the file to split.
		fileDao.openForRead(file).use { fin ->
			logger.debug { "Opened file $file for input." }
			val parentDirectory = file.parentFile

			var index = 0
			var totalBytesCopied = 0L

			// Loop until all the bytes have been copied.
			while (totalBytesCopied < fileDao.getLength(file)) {
				// Generate the chunk file name.
				val outputFile = File(parentDirectory, "%s_%0${INDEX_PADDING}d".format(file.name, index))
				logger.info { "Creating chunk file $outputFile" }
				// Open the chunk file for output.
				fileDao.openForWrite(outputFile).use { fout ->
					totalBytesCopied += copyFileChunk(fin, fout, splitCommand.chunkSize)
					logger.info {"$totalBytesCopied total bytes written." }
				}
				index++
			}
		}

		// Delete the original if requested.
		if (splitCommand.deleteOriginal) {
			logger.info { "Deleting original file $file" }
			fileDao.delete(file)
		}
	}

	override fun combineFiles(combineCommand: CombineCommand) {
		logger.debug { "Calling combineFiles combineCommand: $combineCommand" }
		val outputFile = combineCommand.destinationName.toFile()

		logger.info { "Combining ${combineCommand.paths.size} files into ${combineCommand.destinationName}" }
		fileDao.openForWrite(outputFile).use { fout ->
			combineCommand.paths.sorted().map { it.toFile() }.forEach { file ->
				logger.info { "Combining file $file" }
				fileDao.openForRead(file).use { fin ->
					copyFileChunk(fin, fout, ChunkSize(fileDao.getLength(file)))
				}

				if (combineCommand.deleteChunk) {
					logger.info { "Deleting $file" }
					fileDao.delete(file)
				}
			}
		}

		logger.info {"SHA-256 Hash: ${fileDao.openForRead(outputFile).use { it.sha256Hash().toHex()	}}"}
	}
}