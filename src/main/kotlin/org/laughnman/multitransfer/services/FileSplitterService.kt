package org.laughnman.multitransfer.services

import org.laughnman.multitransfer.models.CombineCommand
import org.laughnman.multitransfer.models.SplitCommand

interface FileSplitterService {

	fun splitFiles(splitCommand: SplitCommand)

	fun combineFiles(combineCommand: CombineCommand)

}