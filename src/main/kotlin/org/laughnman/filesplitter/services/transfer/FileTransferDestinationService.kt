package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.FileDestinationCommand
import org.laughnman.filesplitter.models.transfer.MetaInfo
import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.outputStream

class FileTransferDestinationService(private val command: FileDestinationCommand) : TransferDestinationService {

	override fun write(metaInfo: MetaInfo, input: Sequence<TransferInfo>) {

		val path = if (command.path.isDirectory()) command.path.resolve(metaInfo.fileName) else command.path

		path.outputStream().use { fout ->
			input.forEach { transferInfo ->
				fout.write(transferInfo.buffer, 0, transferInfo.bytesRead)
			}
		}
	}
}