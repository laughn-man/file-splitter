plugins {
	kotlin("jvm") version "1.5.31"
}

group = "org.laughnman"
version = "1.0-SNAPSHOT"

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