package org.laughnman.multitransfer.dao

import mu.KotlinLogging
import org.laughnman.multitransfer.models.s3.S3Url
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer

private val logger = KotlinLogging.logger {}

class S3DaoImpl(private val s3: S3Client) : S3Dao {

	override fun createMultipartUpload(s3Url: S3Url): CreateMultipartUploadResponse {
		logger.debug { "createMultipartUpload s3Url: $s3Url" }

		val createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.createMultipartUpload(createMultipartUploadRequest)
	}

	fun putObject()

	override fun uploadPart(partNum: Int, multipartUploadResponse: CreateMultipartUploadResponse, buffer: ByteBuffer): CompletedPart {
		val uploadPartRequest = UploadPartRequest.builder()
			.bucket(multipartUploadResponse.bucket())
			.key(multipartUploadResponse.key())
			.uploadId(multipartUploadResponse.uploadId())
			.partNumber(partNum)
			.build()

		val eTag = s3.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(buffer)).eTag()
		return CompletedPart.builder()
			.partNumber(partNum)
			.eTag(eTag)
			.build()
	}

	override fun completeMultipartUpload(partList: List<CompletedPart>, multipartUploadResponse: CreateMultipartUploadResponse) {
		val completedMultipartUpload = CompletedMultipartUpload.builder()
			.parts(partList)
			.build()

		val completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
			.bucket(multipartUploadResponse.bucket())
			.key(multipartUploadResponse.key())
			.uploadId(multipartUploadResponse.uploadId())
			.multipartUpload(completedMultipartUpload)
			.build()

		s3.completeMultipartUpload(completeMultipartUploadRequest)
	}

	override fun getObjectInputStream(s3Url: S3Url): ResponseInputStream<GetObjectResponse> {
		val getObjectRequest = GetObjectRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.getObject(getObjectRequest)
	}

	override fun getObjectHead(s3Url: S3Url): HeadObjectResponse {
		val headObjectRequest = HeadObjectRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.headObject(headObjectRequest)
	}

	override fun listObjects(s3Url: S3Url): List<S3Object> {
		val contents = ArrayList<S3Object>()

		var listObjectsRequest = ListObjectsV2Request.builder()
			.bucket(s3Url.bucket)
			.prefix(s3Url.key)
			.build()

		var response = s3.listObjectsV2(listObjectsRequest)
		contents.addAll(response.contents())

		// If there are more results, go pull them.
		while (response.isTruncated) {
			listObjectsRequest = ListObjectsV2Request.builder()
				.continuationToken(response.nextContinuationToken())
				.build()

			response = s3.listObjectsV2(listObjectsRequest)
			contents.addAll(response.contents())
		}

		return contents
	}
}