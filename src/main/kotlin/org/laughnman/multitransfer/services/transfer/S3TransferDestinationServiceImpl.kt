package org.laughnman.multitransfer.services.transfer

import mu.KotlinLogging
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.transfer.*
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse

private val logger = KotlinLogging.logger {}

class S3TransferDestinationServiceImpl(private val s3DestinationCommand: S3DestinationCommand, private val s3Dao: S3Dao) : TransferDestinationService {

	override suspend fun write(): suspend (Transfer) -> Unit {

		lateinit var multipartUploadResponse: CreateMultipartUploadResponse
		val partList: MutableList<CompletedPart> = ArrayList()

		return { transfer ->
			when(transfer) {
				is Start -> {
					multipartUploadResponse = s3Dao.createMultipartUpload(s3DestinationCommand.s3Url)
				}
				is Next -> {
					s3Dao.uploadPart(partList.size + 1, multipartUploadResponse, transfer.minSizeBuffer())
				}
				is Complete -> {
					s3Dao.completeMultipartUpload(partList, multipartUploadResponse)
				}
				is Error -> logger.error(transfer.exception) { "An exception occured on the source." }
			}
		}
	}
}