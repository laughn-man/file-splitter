package org.laughnman.filesplitter.models.transfer

enum class TransferType {
	FILE, ARTIFACTORY
}

interface TransferParameters {

	val type: TransferType

}