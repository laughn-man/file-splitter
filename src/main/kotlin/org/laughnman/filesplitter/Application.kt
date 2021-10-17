package org.laughnman.filesplitter

import ch.qos.logback.classic.Level
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.laughnman.filesplitter.services.StartupService
import org.laughnman.filesplitter.services.servicesModule
import org.slf4j.LoggerFactory

private fun configureLogLevel() {

	val root = LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
	root.level = Level.valueOf(System.getProperty("root-log-level", "ERROR"))

	val logger = LoggerFactory.getLogger("org.laughnman.filesplitter") as ch.qos.logback.classic.Logger
	logger.level = Level.valueOf(System.getProperty("log-level", "INFO"))
}

fun main(args: Array<String>) {

	configureLogLevel()

	startKoin {
		modules(servicesModule)
	}

	Application().startupService.run(args)
}

class Application : KoinComponent {

	val startupService : StartupService = get()

}