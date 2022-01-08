package org.laughnman.multitransfer.models.transfer

import org.laughnman.multitransfer.models.AbstractCommand
import picocli.CommandLine.*
import java.net.URI

@Command(name = "dest-artifactory", description = ["Transferring a file to Artifactory."])
class ArtifactoryDestinationCommand : AbstractCommand() {

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

	@Parameters(description = ["Destination file path in artifactory. This should start at the repo name.",
		"If a file path ends in / it will be treated as a directory and all the files in the directory will be transferred."])
	lateinit var filePath: String

	@Option(names = ["--url"], required = true, description = ["The base Artifactory URL.",
		"Should be the base of the Artifactory URL, not including the repo."])
	lateinit var url: URI

	@Option(names = ["-u", "--user"], description = ["The Artifactory user name."])
	var userName: String = ""

	@ArgGroup(exclusive = true, multiplicity = "1")
	lateinit var exclusive: Exclusive
}