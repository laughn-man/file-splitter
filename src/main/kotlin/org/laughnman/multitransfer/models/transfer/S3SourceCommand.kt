package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeConverter
import org.laughnman.multitransfer.models.ChunkSizeUnit
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.s3.S3UrlConverter
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(name = "src-s3", description = ["Transferring a file from AWS S3."])
class S3SourceCommand : AbstractS3Command() {

	@Parameters(converter = [S3UrlConverter::class])
	lateinit var s3Urls: Array<S3Url>

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 5MB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(5, ChunkSizeUnit.MB)

}
