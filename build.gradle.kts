
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
	kotlin("jvm") version "2.0.21"
	id("com.gradleup.shadow") version "8.3.5"
}

group = "org.laughnman"
version = "0.3.5"

val koinVersion = "4.0.1"
val kotestVersion = "5.7.2"
val ktorVersion = "3.0.3"
val awsVersion = "2.29.44"

repositories {
	mavenCentral()
}

dependencies {
	//implementation(kotlin("stdlib"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.7.6")

	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

	implementation("software.amazon.awssdk:s3:$awsVersion")
	implementation("software.amazon.awssdk:netty-nio-client:$awsVersion")

	// Do not upgrade to 1.4, it does not support Java 8.
	implementation("ch.qos.logback:logback-classic:1.3.15")
	implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
	testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
	testImplementation("io.mockk:mockk:1.13.14")
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_1_8)
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}


tasks {
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

		dependsOn("createProperties")
	}

	// Create a createProperties task that will create a properties file in the resources folder.
	register("createProperties") {
		doLast {
			val resourcesDir = File("${layout.buildDirectory.get()}/resources/main")
			println(resourcesDir)
			resourcesDir.mkdirs()

			File(resourcesDir, "multi-transfer.properties").outputStream().use {fout ->
				val p = Properties()
				p["version"] = project.version.toString()
				p.store(fout, null)
			}
		}
	}

	// Have build dependon shadowJar.
	build {
		dependsOn(shadowJar)
	}

	withType<Test> {
		useJUnitPlatform()
	}
}