
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.0"
	id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "org.laughnman"
version = "0.2.0"

val koinVersion = "3.1.3"
val kotestVersion = "4.6.3"
val ktorVersion = "1.6.6"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")

	implementation("io.insert-koin:koin-core:$koinVersion")
	implementation("info.picocli:picocli:4.6.2")

	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	implementation("io.ktor:ktor-client-jackson:$ktorVersion")

	implementation("ch.qos.logback:logback-classic:1.2.7")
	implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
	testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
	testImplementation("io.mockk:mockk:1.12.1")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
	named<ShadowJar>("shadowJar") {
		archiveClassifier.set("")
		mergeServiceFiles()
		manifest {
			attributes(mapOf(
				"Main-Class" to "org.laughnman.filesplitter.ApplicationKt",
				"Version" to project.version
			))
		}
	}
}

tasks {
	build {
		dependsOn(shadowJar)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}