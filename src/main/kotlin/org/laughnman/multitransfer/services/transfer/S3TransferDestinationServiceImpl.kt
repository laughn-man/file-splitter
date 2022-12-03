package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.utilities.BufferManager
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse

const val MULTIPART_UPLOAD_SIZE_THRESHOLD = 10_000_000

private val logger = KotlinLogging.logger {}

class S3TransferDestinationServiceImpl(private val s3DestinationCommand: S3DestinationCommand, private val s3Dao: S3Dao) : TransferDestinationService {

	override suspend fun write(): suspend (Transfer) -> Unit {

		lateinit var multipartUploadResponse: CreateMultipartUploadResponse
		lateinit var s3Url: S3Url
		var doMultiPartUpload = false
		val bufferManager = BufferManager(s3DestinationCommand.bufferSize.toBytes().toInt())
		val partList: MutableList<CompletedPart> = ArrayList()

		return { transfer ->
			when(transfer) {
				is Start -> {
					logger.debug { "Starting uploaded metaInfo:${transfer.metaInfo}" }

					s3Url = if (s3DestinationCommand.s3Url.key.isEmpty()) {
						s3DestinationCommand.s3Url + transfer.metaInfo.fileName
					} else {
						s3DestinationCommand.s3Url
					}

					doMultiPartUpload = transfer.metaInfo.fileSize >= MULTIPART_UPLOAD_SIZE_THRESHOLD

					if (doMultiPartUpload) {
						multipartUploadResponse = s3Dao.createMultipartUploadAsync(s3Url).await()
					}
				}
				is Next -> {
					bufferManager.put(transfer) {buffer ->
						if (doMultiPartUpload) {
							val part = s3Dao.uploadPartAsync(partList.size + 1, multipartUploadResponse, buffer).await()
							partList.add(part)
						}
					}
				}
				is Complete -> {
					logger.debug { "Received Complete message. bufferManager: ${bufferManager.buffer.remaining()}" }
					if (doMultiPartUpload) {
						s3Dao.completeMultipartUploadAsync(partList, multipartUploadResponse).await()
					}
					else {
						s3Dao.uploadObjectAsync(s3Url, bufferManager.buffer).await()
					}
				}
				is Error -> logger.error(transfer.exception) { "An exception occured on the source." }
			}
		}
	}
}