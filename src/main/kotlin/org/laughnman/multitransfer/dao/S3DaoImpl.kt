package org.laughnman.multitransfer.dao

import software.amazon.awssdk.services.s3.S3Client

class S3DaoImpl(private val s3Client: S3Client) : S3Dao {

	override fun uploadObject(s3Uri: String, block: (buffer: ByteArray, length: Int) -> Unit) {
		TODO("Not yet implemented")
	}

	override fun downloadObject(s3Uri: String, block: (buffer: ByteArray) -> Unit) {
		TODO("Not yet implemented")
	}
}