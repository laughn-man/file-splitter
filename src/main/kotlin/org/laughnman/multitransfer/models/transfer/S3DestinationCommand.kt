package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeConverter
import org.laughnman.multitransfer.models.ChunkSizeUnit
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.s3.S3UrlConverter
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "dest-s3", description = ["Transferring a file to AWS S3."])
class S3DestinationCommand : AbstractS3Command() {

	@Parameters(converter = [S3UrlConverter::class])
	lateinit var s3Url: S3Url

	@CommandLine.Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 10MB. This is the minimum allowed transfer size. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(10, ChunkSizeUnit.MB)
}