package org.laughnman.filesplitter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.laughnman.filesplitter.services.StartupService
import org.laughnman.filesplitter.services.servicesModule
import org.slf4j.LoggerFactory

private const val FULL_PATTERN = "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
private const val SHORT_PATTERN = "%msg%n"

private fun configureLogLevel() {

	val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
	val pattern = System.getProperty("log-pattern", "short")

	val encoder = PatternLayoutEncoder()
	encoder.context = loggerContext
	encoder.pattern = if (pattern.equals("full", true)) FULL_PATTERN else SHORT_PATTERN
	encoder.start()

	val appender = ConsoleAppender<ILoggingEvent>()
	appender.name = "CONSOLE"
	appender.context = loggerContext
	appender.encoder = encoder
	appender.start()

	val root = LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
	root.addAppender(appender)
	root.level = Level.valueOf(System.getProperty("root-log-level", "ERROR"))

	val logger = LoggerFactory.getLogger("org.laughnman.filesplitter") as ch.qos.logback.classic.Logger
	logger.addAppender(appender)
	logger.isAdditive = false
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