package org.laughnman.filesplitter.services.transfer

import org.laughnman.filesplitter.models.transfer.FileSourceCommand
import org.laughnman.filesplitter.models.transfer.TransferInfo

class FileTransferSource(private val command: FileSourceCommand) : TransferSourceService {

	override fun read(): List<Sequence<TransferInfo>> {
		command.filePaths.forEach { path ->

		}
	}
}