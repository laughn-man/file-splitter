package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.flow
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.utilities.findFileName
import org.laughnman.multitransfer.utilities.readAsSequence

class S3TransferSourceServiceImpl(private val command: S3SourceCommand, private val s3Dao: S3Dao) : TransferSourceService {

	private fun buildMetaInfo(s3Url: S3Url, size: Long) = MetaInfo(fileName = s3Url.key.findFileName(), fileSize = size)

	private fun buildFlow(metaInfo: MetaInfo, s3Url: S3Url) = flow {
		try {
			s3Dao.getObjectInputStream(s3Url).use { oin ->
				emit(Start(metaInfo))

				oin.readAsSequence(command.bufferSize.toBytes().toInt()).forEach { (readLength, buffer) ->
					emit(Next(metaInfo, buffer, readLength))
				}

				emit(Complete(metaInfo))
			}
		}
		catch (e: Exception) {
			emit(Error(metaInfo, e))
		}
	}

	override fun read() = flow {
		command.s3Urls.forEach { s3Url ->
			if (s3Url.key.endsWith("/")) {
				s3Dao.listObjects(s3Url).forEach {s3Object ->
					val newS3Url = S3Url(s3Url.bucket, s3Object.key())
					val metaInfo = buildMetaInfo(newS3Url, s3Object.size())
					emit(buildFlow(metaInfo, s3Url))
				}
			}
			else {
				val headInfo = s3Dao.getObjectHead(s3Url)
				val metaInfo = buildMetaInfo(s3Url, headInfo.contentLength())
				emit(buildFlow(metaInfo, s3Url))
			}
		}
	}
}