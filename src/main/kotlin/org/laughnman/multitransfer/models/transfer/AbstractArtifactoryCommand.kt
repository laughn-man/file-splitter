package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Option
import java.net.URI

abstract class AbstractArtifactoryCommand : AbstractCommand() {

	class Exclusive {
		@Option(names = ["-p", "--password"], interactive = true, arity = "0..1", description = [
			"The Artifactory user password.",
			"If the value is left blank the password will be requested on STDIN."])
		var password: String = ""

		@Option(names = ["-t", "--token"], interactive = true, arity = "0..1", description = [
			"The Artifactory token.",
			"If the value is left blank the token will be requested on STDIN."])
		var token: String = ""
	}

	@Option(names = ["--url"], required = true, description = ["The base Artifactory URL.",
		"Should be the base of the Artifactory URL, not including the repo."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@ArgGroup(exclusive = true, multiplicity = "1")
	lateinit var exclusive: Exclusive

	@Option(names = ["--request-timeout"], description = ["The timeout in seconds to wait on the request.",
		"Defaults to no timeout."])
	var requestTimeout: Long = Long.MAX_VALUE

	@Option(names = ["--insecure"], description = [
		"When set multi-transfer will ignore the authenticity of Artifactory's SSL certs and assume they are genuine.",
		"Transfers are still encrypted in transit even though the source or destination is not verified.",
		"This should only be used when you are confident of the endpoint you are connecting to."])
	var insecure: Boolean = false
}