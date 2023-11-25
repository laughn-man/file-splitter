package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.s3.S3UrlConverter
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "src-s3", description = ["Transferring a file from AWS S3."])
class S3SourceCommand : AbstractS3Command() {

	@Parameters(converter = [S3UrlConverter::class], description = ["List of S3 url entries to read from. Entries must start with s3://"])
	lateinit var s3Urls: Array<S3Url>

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as S3SourceCommand

		return s3Urls.contentEquals(other.s3Urls)
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + s3Urls.contentHashCode()
		return result
	}
}
