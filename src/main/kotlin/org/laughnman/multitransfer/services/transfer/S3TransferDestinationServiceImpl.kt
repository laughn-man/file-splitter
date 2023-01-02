package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.*
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse

const val MULTIPART_UPLOAD_SIZE_THRESHOLD = 10_485_760

private val logger = KotlinLogging.logger {}

class S3TransferDestinationServiceImpl(private val s3DestinationCommand: S3DestinationCommand, private val s3Dao: S3Dao) : TransferDestinationService {

	override suspend fun write(): DestinationWriter {

		lateinit var multipartUploadResponse: CreateMultipartUploadResponse
		lateinit var s3Url: S3Url
		lateinit var metaInfo: MetaInfo
		var doMultiPartUpload = false
		val partList: MutableList<CompletedPart> = ArrayList()

		return { buffer, transfer ->

			if (buffer.capacity() < MULTIPART_UPLOAD_SIZE_THRESHOLD) {
				throw IllegalArgumentException("Buffer size must be at 10 MB in order to be transferred with S3.")
			}

			when(transfer) {
				is Start -> {
					metaInfo = transfer.metaInfo

					s3Url = if (s3DestinationCommand.s3Url.isFolder()) {
						s3DestinationCommand.s3Url + metaInfo.fileName
					} else {
						s3DestinationCommand.s3Url
					}

					logger.info { "Writing to S3 key $s3Url." }

					doMultiPartUpload = metaInfo.fileSize >= MULTIPART_UPLOAD_SIZE_THRESHOLD

					logger.debug { "Performing multipart upload: $doMultiPartUpload." }

					if (doMultiPartUpload) {
						multipartUploadResponse = s3Dao.createMultipartUploadAsync(s3Url).await()
					}
				}
				is Next -> {
					if (doMultiPartUpload) {
						val part = s3Dao.uploadPartAsync(partList.size + 1, multipartUploadResponse, buffer).await()
						partList.add(part)
					}
					else {
						s3Dao.uploadObjectAsync(s3Url, buffer).await()
					}
				}
				is Complete -> {
					if (doMultiPartUpload) {
						s3Dao.completeMultipartUploadAsync(partList, multipartUploadResponse).await()
					}
				}
				is Error -> logger.error(transfer.exception) { "An exception occured on the source." }
			}
		}
	}
}