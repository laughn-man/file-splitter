package org.laughnman.multitransfer.models

import picocli.CommandLine.Option
import picocli.CommandLine.Command
import picocli.CommandLine.ArgGroup

@Command(name = "transfer", aliases = ["copy"], subcommandsRepeatable = true, description = ["Copies a source to a destination."])
class TransferCommand : AbstractCommand() {

	class TrustStoreParams {
		@Option(names = ["--trust-store"], required = true, description = [
			"Points to a Java Keystore with trusted certificates that multi-transfer " ,
			"should use in place of the default default certificates installed with Java."])
		var trustStore: String = ""

		@Option(names = ["--trust-store-password"], interactive = true, arity = "0..1", required = true, description = [
			"The password to the trust store defined in --trust-store.",
			"If the value is left blank the password will be requested on STDIN."])
		var trustStorePassword: String = ""
	}

	class SslParams {
		@Option(names = ["--insecure"], required = true, description = [
			"When set multi-transfer will ignore the authenticity of any SSL certs and assume they are genuine.",
			"Transfers are still encrypted in transit even though the source or destination is not verified.",
			"This should only be used when you are confident of the endpoint you are connecting to."])
		var insecure: Boolean = false

		@ArgGroup(exclusive = false, multiplicity = "0..1")
		var trustStoreParams: TrustStoreParams = TrustStoreParams()
	}

	@Option(names = ["-p", "--parallelism"], description = ["The number of transfers to perform in parallel."])
	var parallelism = 1

	@ArgGroup(exclusive = true, multiplicity = "0..1")
	var sslParams: SslParams = SslParams()
}