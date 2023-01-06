package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Option

abstract class AbstractS3Command : AbstractCommand() {

	class Access {
		@Option(names = ["--access-key"], required = true, description = ["AWS access key.", "Profile access key will be used if not provided."])
		var accessKey: String = ""

		@Option(names = ["--access-secret"], required = true, interactive = true, arity = "0..1", description = ["AWS access key secret.",
		"Profile access key secret will be used if not provided."])
		var accessSecret: String = ""
	}

	class Exclusive {
		@Option(names = ["-p", "--profile"], description = ["The AWS profile to use. The default profile is used if not passed."])
		var profile: String = ""

		@ArgGroup(exclusive = false)
		lateinit var access: Access
	}

	@Option(names = ["-r", "--region"], description = ["The AWS region. The default region is used if not passed."])
	var region: String = ""

	@Option(names = ["--endpoint"], description = ["Overrides the default AWS endpoint."])
	var endpoint: String = ""

	@ArgGroup(exclusive = true)
	var exclusive: Exclusive? = null
}