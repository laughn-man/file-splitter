package org.laughnman.multitransfer.services.transfer

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import org.laughnman.multitransfer.dao.S3Dao
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.transfer.*
import org.laughnman.multitransfer.utilities.findFileName
import org.laughnman.multitransfer.utilities.readAsSequence
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response

class S3TransferSourceServiceImpl(private val command: S3SourceCommand, private val s3Dao: S3Dao) : TransferSourceService {

	private fun buildMetaInfo(s3Url: S3Url, size: Long) = MetaInfo(fileName = s3Url.key.findFileName(), fileSize = size)

	private fun buildFlow(metaInfo: MetaInfo, s3Url: S3Url): SourceReader = { buffer ->
		flow {
			try {
				s3Dao.getObjectPublisherAsync(s3Url).await().use { oin ->
					emit(Start(metaInfo))

					oin.readAsSequence(buffer.capacity()).forEach { (readLength, byteArr) ->
						buffer.put(byteArr, 0, readLength)
						emit(Next)
					}

					emit(Complete)
				}
			} catch (e: Exception) {
				emit(Error(e))
			}
		}
	}

	private suspend fun processS3List(flowCollector: FlowCollector<SourceReader>, response: ListObjectsV2Response) {
		response.contents()
			// Remove any folder only keys.
			.filterNot { it.key().isEmpty() }
			.forEach {s3Object ->
				val s3Url = S3Url(response.name(), s3Object.key())
				val metaInfo = buildMetaInfo(s3Url, s3Object.size())
				flowCollector.emit(buildFlow(metaInfo, s3Url))
		}
	}

	override fun read() = flow {
		command.s3Urls.forEach { s3Url ->
			// If processing a directory we need to list each key first.
			if (s3Url.isFolder()) {
				var response = s3Dao.listObjectsAsync(s3Url).await()
				processS3List(this, response)

				while (response.isTruncated) {
					response = s3Dao.listObjectsAsync(response.nextContinuationToken()).await()
					processS3List(this, response)
				}
			}
			else {
				val headInfo = s3Dao.getObjectHeadAsync(s3Url).await()
				val metaInfo = buildMetaInfo(s3Url, headInfo.contentLength())
				emit(buildFlow(metaInfo, s3Url))
			}
		}
	}
}