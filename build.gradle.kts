
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
	kotlin("jvm") version "1.9.10"
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.laughnman"
version = "0.3.3"

val koinVersion = "3.4.3"
val kotestVersion = "5.7.2"
val ktorVersion = "2.3.4"
val awsVersion = "2.20.143"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.7.5")

	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

	implementation("software.amazon.awssdk:s3:$awsVersion")
	implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")

	// Do not upgrade to 1.4, it does not support Java 8.
	implementation("ch.qos.logback:logback-classic:1.3.11")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
	testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
	testImplementation("io.mockk:mockk:1.13.7")
}


tasks {
	// Enforce 1.8 on both the main and test compiles.
	withType<KotlinCompile>().configureEach {
		kotlinOptions.jvmTarget = "1.8"
	}
	withType<JavaCompile>().configureEach {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
	}

	shadowJar {
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