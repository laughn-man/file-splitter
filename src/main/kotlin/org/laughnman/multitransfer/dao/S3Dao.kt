package org.laughnman.multitransfer.dao

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.S3Command
import org.laughnman.multitransfer.models.transfer.TransferInfo
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

interface S3Dao {

	companion object Factory {

		fun fromCommand(s3Command: S3Command): S3Dao {
			val s3Builder = S3Client.builder()

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
					val awsCreds = AwsBasicCredentials.create(it.access.accessKey, it.access.accessKey)
					s3Builder.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				}
			}

			return S3DaoImpl(s3Builder.build())
		}
	}

	// See https://github.com/awsdocs/aws-doc-sdk-examples/blob/574f7e288b2208b60a3e9f8274342a64c6a7ce31/javav2/example_code/s3/src/main/java/com/example/s3/S3ObjectOperations.java#L213
	fun uploadObject(s3Url: S3Url, flow: Flow<TransferInfo>)

	fun downloadObject(s3Url: S3Url, block: (buffer: ByteArray) -> Unit)

}