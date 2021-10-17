package org.laughnman.filesplitter.services

import org.laughnman.filesplitter.models.ChunkSize
import org.laughnman.filesplitter.models.CombineCommand
import org.laughnman.filesplitter.models.SplitCommand
import java.nio.file.Path

interface FileSplitterService {

	fun splitFiles(splitCommand: SplitCommand)

	fun combineFiles(combineCommand: CombineCommand)

}