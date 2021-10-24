package org.laughnman.filesplitter.utilities

import org.laughnman.filesplitter.models.transfer.TransferParameters

enum class Direction {
	SOURCE, DESTINATION
}

interface TransferParametersParser {

	fun <T: TransferParameters> parse(direction: Direction, parameters: String): T

}