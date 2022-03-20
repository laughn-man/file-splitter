package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.transform
import mu.KotlinLogging
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.S3DestinationCommand
import org.laughnman.multitransfer.models.transfer.Transfer
import org.laughnman.multitransfer.models.transfer.TransferInfo

private val logger = KotlinLogging.logger {}

class S3TransferDestinationServiceImpl(private val s3DestinationCommand: S3DestinationCommand, private val s3Dao: S3Dao) : TransferDestinationService {

	override suspend fun write(metaInfo: MetaInfo, input: Flow<Transfer>) {
		logger.debug { "Calling write with metaInfo: $metaInfo" }

	}
}