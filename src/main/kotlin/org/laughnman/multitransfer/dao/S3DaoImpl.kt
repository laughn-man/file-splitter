package org.laughnman.multitransfer.dao

import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.Next
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*

private val logger = KotlinLogging.logger {}

class S3DaoImpl(private val s3: S3Client) : S3Dao {

	override suspend fun uploadObject(s3Url: S3Url, flow: Flow<Next>) {
		logger.debug { "uploadObject s3Url: $s3Url" }

		val createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		val response = s3.createMultipartUpload(createMultipartUploadRequest)
		val uploadId = response.uploadId()

		logger.debug { "uploadId: $uploadId" }

		val partList: MutableList<CompletedPart> = ArrayList()

		flow.collect { transferInfo ->
			val partNum = partList.size + 1
			val uploadPartRequest = UploadPartRequest.builder()
				.bucket(s3Url.bucket)
				.key(s3Url.key)
				.uploadId(uploadId)
				.partNumber(partNum)
				.build()

			val eTag = s3.uploadPart(uploadPartRequest, RequestBody.fromBytes(transferInfo.minSizeBuffer())).eTag()
			val part = CompletedPart.builder()
				.partNumber(partNum)
				.eTag(eTag)
				.build()

			partList.add(part)
		}

		val completedMultipartUpload = CompletedMultipartUpload.builder()
			.parts(partList)
			.build()

		val completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.uploadId(uploadId)
			.multipartUpload(completedMultipartUpload)
			.build()


		s3.completeMultipartUpload(completeMultipartUploadRequest)
	}

	override fun downloadObject(s3Url: S3Url, block: (buffer: ByteArray) -> Unit) {
		TODO("Not yet implemented")
	}
}