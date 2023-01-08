
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
	kotlin("jvm") version "1.7.21"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.laughnman"
version = "0.3.2"

val koinVersion = "3.3.2"
val kotestVersion = "5.5.4"
val ktorVersion = "2.2.1"
val awsVersion = "2.19.8"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.7.0")

	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

	implementation("software.amazon.awssdk:s3:$awsVersion")
	implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")

	// Do not upgrade to 1.4, it does not support Java 8.
	implementation("ch.qos.logback:logback-classic:1.3.5")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
	testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
	testImplementation("io.mockk:mockk:1.13.2")
}

tasks {
	named<ShadowJar>("shadowJar") {
		archiveClassifier.set("")
		archiveVersion.set("")
		mergeServiceFiles()
		manifest {
			attributes(mapOf(
				"Main-Class" to "org.laughnman.multitransfer.ApplicationKt",
				"Version" to project.version
			))
		}
	}

	// Create a createProperties take that will create a properties file in the resources folder.
	register("createProperties") {
		doLast {
			val resourcesDir = File("$buildDir/resources/main")
			resourcesDir.mkdirs()

			File(resourcesDir, "multi-transfer.properties").outputStream().use {fout ->
				val p = Properties()
				p["version"] = project.version.toString()
				p.store(fout, null)
			}
		}
	}

	// Enforce 1.8 on both the main and test compiles.
	withType<KotlinCompile>().configureEach {
		kotlinOptions.jvmTarget = "1.8"
	}

	// Have the classes task depend on createProperties.
	classes {
		dependsOn("createProperties")
	}

	// Have build dependon shadowJar.
	build {
		dependsOn(shadowJar)
	}

	withType<Test> {
		useJUnitPlatform()
	}
}