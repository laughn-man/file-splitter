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

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Access

			if (accessKey != other.accessKey) return false
			if (accessSecret != other.accessSecret) return false

			return true
		}

		override fun hashCode(): Int {
			var result = accessKey.hashCode()
			result = 31 * result + accessSecret.hashCode()
			return result
		}
	}

	class Exclusive {
		@Option(names = ["-p", "--profile"], description = ["The AWS profile to use. The default profile is used if not passed."])
		var profile: String = ""

		@ArgGroup(exclusive = false)
		var access: Access = Access()

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Exclusive

			if (profile != other.profile) return false
			if (access != other.access) return false

			return true
		}

		override fun hashCode(): Int {
			var result = profile.hashCode()
			result = 31 * result + access.hashCode()
			return result
		}
	}

	@Option(names = ["-r", "--region"], description = ["The AWS region. The default region is used if not passed."])
	var region: String = ""

	@Option(names = ["--endpoint"], description = ["Overrides the default AWS endpoint."])
	var endpoint: String = ""

	@ArgGroup(exclusive = true)
	var exclusive: Exclusive? = null

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		if (!super.equals(other)) return false

		other as AbstractS3Command

		if (region != other.region) return false
		if (endpoint != other.endpoint) return false
		if (exclusive != other.exclusive) return false

		return true
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + region.hashCode()
		result = 31 * result + endpoint.hashCode()
		result = 31 * result + (exclusive?.hashCode() ?: 0)
		return result
	}
}