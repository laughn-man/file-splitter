package org.laughnman.multitransfer.dao

import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.AbstractS3Command
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

interface S3Dao {

	companion object Factory {

		fun fromCommand(s3Command: AbstractS3Command): S3Dao {

			val s3Builder = S3AsyncClient.builder()
				.httpClientBuilder(NettyNioAsyncHttpClient.builder())

			if (s3Command.region.isNotBlank()) {
				s3Builder.region(Region.of(s3Command.region))
			}

			if (s3Command.endpoint.isNotBlank()) {
				s3Builder.endpointOverride(URI.create(s3Command.endpoint))
			}

			s3Command.exclusive?.let {
				if (it.profile.isNotBlank()) {
					s3Builder.credentialsProvider(ProfileCredentialsProvider.create(it.profile))
				}
				else {
					val awsCreds = AwsBasicCredentials.create(it.access.accessKey, it.access.accessSecret)
					s3Builder.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				}
			}

			return S3DaoImpl(s3Builder.build())
		}
	}

	// See https://github.com/awsdocs/aws-doc-sdk-examples/blob/574f7e288b2208b60a3e9f8274342a64c6a7ce31/javav2/example_code/s3/src/main/java/com/example/s3/S3ObjectOperations.java#L213
	fun createMultipartUploadAsync(s3Url: S3Url): CompletableFuture<CreateMultipartUploadResponse>
	fun uploadPartAsync(partNum: Int, multipartUploadResponse: CreateMultipartUploadResponse, buffer: ByteBuffer): CompletableFuture<CompletedPart>
	fun completeMultipartUploadAsync(partList: List<CompletedPart>, multipartUploadResponse: CreateMultipartUploadResponse): CompletableFuture<CompleteMultipartUploadResponse>

	fun uploadObjectAsync(s3Url: S3Url, contents: ByteBuffer): CompletableFuture<PutObjectResponse>
	fun getObjectHeadAsync(s3Url: S3Url): CompletableFuture<HeadObjectResponse>
	fun listObjectsAsync(s3Url: S3Url): CompletableFuture<ListObjectsV2Response>
	fun listObjectsAsync(continuationToken: String): CompletableFuture<ListObjectsV2Response>
	fun getObjectPublisherAsync(s3Url: S3Url): CompletableFuture<ResponseInputStream<GetObjectResponse>>

}