package org.laughnman.multitransfer.utilities

import picocli.CommandLine
import java.util.Properties

class VersionProvider : CommandLine.IVersionProvider {

	override fun getVersion(): Array<String> {

		javaClass.classLoader.getResourceAsStream("multi-transfer.properties").use {fin ->
			val properties = Properties()
			properties.load(fin)

			val version = properties["version"]
			return arrayOf("Multi-Transfer version $version")
		}
	}
}