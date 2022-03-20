package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import org.laughnman.multitransfer.models.ChunkSize
import org.laughnman.multitransfer.models.ChunkSizeConverter
import org.laughnman.multitransfer.models.ChunkSizeUnit
import org.laughnman.multitransfer.models.s3.S3Url
import org.laughnman.multitransfer.models.s3.S3UrlConverter
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import picocli.CommandLine.ArgGroup

class S3DestinationCommand : AbstractS3Command() {


	@Parameters(converter = [S3UrlConverter::class])
	lateinit var s3Uri: S3Url

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}