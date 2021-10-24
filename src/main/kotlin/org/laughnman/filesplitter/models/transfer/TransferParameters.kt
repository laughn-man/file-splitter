package org.laughnman.filesplitter.models.transfer

enum class TransferType {
	FILE, ARTIFACTORY, TESTER
}

interface TransferParameters {

	val type: TransferType

}