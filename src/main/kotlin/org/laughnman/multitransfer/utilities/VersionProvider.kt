package org.laughnman.multitransfer.utilities

import picocli.CommandLine
import java.util.jar.Manifest

class VersionProvider : CommandLine.IVersionProvider {

	override fun getVersion(): Array<String> {
		val manifest = Manifest(this.javaClass.classLoader.getResourceAsStream("META-INF/MANIFEST.MF"))
		val version = manifest.mainAttributes.getValue("Version")

		return arrayOf("Multi-Transfer version $version")
	}
}