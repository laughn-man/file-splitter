package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.ChunkSize
import org.laughnman.filesplitter.models.CombineCommand
import org.laughnman.filesplitter.models.SplitCommand
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

private const val BUFFER_SIZE: Int = 4_096
private const val INDEX_PADDING: Int = 6

class FileSplitterServiceImpl : FileSplitterService {

	private val logger = LoggerFactory.getLogger(this::class.java)

	private fun copyFileChunk(fin: InputStream, fout: OutputStream, chunkSize: ChunkSize): Long {
		val buffer = ByteArray(BUFFER_SIZE)
		val maxBytes = chunkSize.toBytes()
		var bytesCopied = 0L

		while (bytesCopied < maxBytes) {
			val length = fin.read(buffer, 0, buffer.size)

			if (length == -1) {
				break
			}

			fout.write(buffer, 0, length)
			bytesCopied += length
		}

		return bytesCopied
	}

	override fun splitFiles(splitCommand: SplitCommand) {
		logger.debug("Calling splitFiles splitCommand: $splitCommand")

		val file = splitCommand.path.toFile()

		if (!file.isFile) {
			throw RuntimeException("$file must be a file.")
		}
		if (file.length() <= splitCommand.chunkSize.toBytes()) {
			throw RuntimeException("$file length of ${file.length()} is less than or equal to chunk size. Nothing to split.")
		}

		// Open the file to split.
		file.inputStream().use { fin ->
			logger.debug("Opened file $file for input.")
			val parentDirectory = file.parentFile

			var index = 0
			var totalBytesCopied = 0L

			// Loop until all the bytes have been copied.
			while (totalBytesCopied < file.length()) {
				logger.debug("Remaining bytes: ${totalBytesCopied}.")
				// Generate the chunk file name.
				val outputFile = File(parentDirectory, "%s_%0${INDEX_PADDING}d".format(file.name, index))
				logger.info("Creating chunk file $outputFile")
				// Open the chunk file for output.
				outputFile.outputStream().use { fout ->
					totalBytesCopied += copyFileChunk(fin, fout, splitCommand.chunkSize)
					logger.info("$totalBytesCopied bytes written.")
				}
				index++
			}
		}

		// Delete the original if requested.
		if (splitCommand.deleteOriginal) {
			logger.info("Deleting original file $file")
			file.delete()
		}
	}

	override fun combineFiles(combineCommand: CombineCommand) {
		logger.debug("Calling combineFiles combineCommand: $combineCommand")

		if (!combineCommand.rootDir.isDirectory()) {
			throw RuntimeException("${combineCommand.rootDir} must be directory.")
		}

		File(combineCommand.rootDir.toFile(), combineCommand.destinationName).outputStream().use { fout ->
			Files.newDirectoryStream(combineCommand.rootDir, combineCommand.chunkPattern).use { dirStream ->
				dirStream.sorted().map { it.toFile() }.forEach { file ->
					logger.info("Combining file $file")
					file.inputStream().use { fin ->
						copyFileChunk(fin, fout, ChunkSize(file.length()))
					}

					if (combineCommand.deleteChunk) {
						file.delete()
					}
				}
			}
		}
	}
}