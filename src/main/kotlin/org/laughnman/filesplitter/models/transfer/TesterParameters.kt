package org.laughnman.filesplitter.models.transfer

import org.laughnman.filesplitter.models.transfer.TransferType.TESTER

data class TesterParameters (
	override val type: TransferType = TESTER,
	val intField: Int,
	val longField: Long,
	val floatField: Float,
	val doubleField: Double,
	val stringField: String,
	val optionalField: String = "test"
) : TransferParameters
