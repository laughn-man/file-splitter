package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.Flow
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.transfer.MetaInfo
import org.laughnman.multitransfer.models.transfer.S3Command
import org.laughnman.multitransfer.models.transfer.TransferInfo

class S3TransferSourceService(private val command: S3Command, private val s3Dao: S3Dao) : TransferSourceService {

	override fun read(): Flow<Pair<MetaInfo, Flow<TransferInfo>>> {

		TODO("Not yet implemented")
	}
}