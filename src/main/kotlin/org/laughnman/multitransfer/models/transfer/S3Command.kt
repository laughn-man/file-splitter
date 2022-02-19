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

class S3Command : AbstractCommand() {

	class Access {
		@Option(names = ["--access-key"], required = true, description = ["AWS access key."])
		var accessKey: String = ""

		@Option(names = ["--access-secret"], required = true, interactive = true, arity = "0..1", description = ["AWS access key secret."])
		var accessSecret: String = ""
	}

	class Exclusive {
		@Option(names = ["-p", "--profile"], description = ["The AWS region. The AWS profile to use. The default profile is used if not passed."])
		var profile: String = ""

		@ArgGroup(exclusive = false)
		lateinit var access: Access
	}

	@Parameters(converter = [S3UrlConverter::class])
	lateinit var s3Uri: S3Url

	@Option(names = ["-r", "--region"], description = ["The AWS region. The default region is used if not passed."])
	var region: String = ""

	@Option(names = ["--endpoint"], description = ["Overrides the default AWS endpoint."])
	var endpoint: String = ""

	@ArgGroup(exclusive = true)
	var exclusive: Exclusive? = null

	@Option(names = ["-b", "--buffer-size"], converter = [ChunkSizeConverter::class],
		description = ["The size of the buffer. Default is 4KB. Format is in <numeric size>B|KB|MB|GB|TB."])
	var bufferSize = ChunkSize(4, ChunkSizeUnit.KB)

}