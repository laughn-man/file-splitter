package org.laughnman.multitransfer.dao

import io.github.oshai.kotlinlogging.KotlinLogging
import org.laughnman.multitransfer.models.s3.S3Url
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

class S3DaoImpl(private val s3: S3AsyncClient) : S3Dao {

	override fun createMultipartUploadAsync(s3Url: S3Url): CompletableFuture<CreateMultipartUploadResponse> {
		logger.debug { "createMultipartUpload s3Url: $s3Url" }

		val createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.createMultipartUpload(createMultipartUploadRequest)
	}

	override fun uploadPartAsync(partNum: Int, multipartUploadResponse: CreateMultipartUploadResponse, buffer: ByteBuffer): CompletableFuture<CompletedPart> {
		val uploadPartRequest = UploadPartRequest.builder()
			.bucket(multipartUploadResponse.bucket())
			.key(multipartUploadResponse.key())
			.uploadId(multipartUploadResponse.uploadId())
			.partNumber(partNum)
			.build()

		return s3.uploadPart(uploadPartRequest, AsyncRequestBody.fromByteBuffer(buffer))
			.thenApplyAsync {
				CompletedPart.builder()
					.partNumber(partNum)
					.eTag(it.eTag())
					.build()
			}
	}

	override fun completeMultipartUploadAsync(partList: List<CompletedPart>, multipartUploadResponse: CreateMultipartUploadResponse): CompletableFuture<CompleteMultipartUploadResponse> {
		val completedMultipartUpload = CompletedMultipartUpload.builder()
			.parts(partList)
			.build()

		val completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
			.bucket(multipartUploadResponse.bucket())
			.key(multipartUploadResponse.key())
			.uploadId(multipartUploadResponse.uploadId())
			.multipartUpload(completedMultipartUpload)
			.build()

		return s3.completeMultipartUpload(completeMultipartUploadRequest)
	}

	override fun uploadObjectAsync(s3Url: S3Url, contents: ByteBuffer): CompletableFuture<PutObjectResponse> {
		val putObjectRequest = PutObjectRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.putObject(putObjectRequest, AsyncRequestBody.fromByteBuffer(contents))
	}

	override fun getObjectPublisherAsync(s3Url: S3Url): CompletableFuture<ResponseInputStream<GetObjectResponse>> {
		val getObjectRequest = GetObjectRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.getObject(getObjectRequest, AsyncResponseTransformer.toBlockingInputStream())
	}

	override fun getObjectHeadAsync(s3Url: S3Url): CompletableFuture<HeadObjectResponse> {
		val headObjectRequest = HeadObjectRequest.builder()
			.bucket(s3Url.bucket)
			.key(s3Url.key)
			.build()

		return s3.headObject(headObjectRequest)
	}

	override fun listObjectsAsync(s3Url: S3Url): CompletableFuture<ListObjectsV2Response> {
		val listObjectsRequest = ListObjectsV2Request.builder()
			.bucket(s3Url.bucket)
			.prefix(s3Url.key)
			.build()

		return s3.listObjectsV2(listObjectsRequest)
	}

	override fun listObjectsAsync(continuationToken: String): CompletableFuture<ListObjectsV2Response> {
		val listObjectsRequest = ListObjectsV2Request.builder()
			.continuationToken(continuationToken)
			.build()

		return s3.listObjectsV2(listObjectsRequest)
	}
}