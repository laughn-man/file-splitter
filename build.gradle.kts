
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "1.6.0"
	id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.laughnman"
version = "0.1.2"

val koinVersion = "3.1.3"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.6.2")
	implementation("ch.qos.logback:logback-classic:1.2.7")
	implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
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