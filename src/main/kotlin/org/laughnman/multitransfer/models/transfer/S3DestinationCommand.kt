package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.s3.S3UrlConverter
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "dest-s3", description = ["Transferring a file to AWS S3."])
class S3DestinationCommand : AbstractS3Command() {

	@Parameters(converter = [S3UrlConverter::class], description = ["S3 url to write to. Entry must start with s3://"])
	lateinit var s3Url: S3Url
}