
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "1.5.31"
	id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.laughnman"
version = "0.1.${ if (project.hasProperty("buildNumber")) project.property("buildNumber") else "0"}"

val koinVersion = "3.1.2"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.6.1")
	implementation("ch.qos.logback:logback-classic:1.2.6")
}

tasks {
	named<ShadowJar>("shadowJar") {
		archiveClassifier.set("")
		mergeServiceFiles()
		manifest {
			attributes(mapOf("Main-Class" to "org.laughnman.filesplitter.ApplicationKt"))
		}
	}
}

tasks {
	build {
		dependsOn(shadowJar)
	}
}