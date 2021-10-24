package org.laughnman.filesplitter.utilities

import jdk.internal.joptsimple.internal.Messages
import mu.KotlinLogging
import org.laughnman.filesplitter.models.transfer.TransferParameters
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter


private const val BASE_PACKAGE = "org.laughnman.filesplitter.models.transfer"


private val logger = KotlinLogging.logger {}

class TransferParametersParserImpl : TransferParametersParser {

	private fun validateParameters(paramsMap: Map<String, String>, kParams: List<KParameter>) {
		val missingParamsMessage = kParams.filterNot { it.isOptional }
			.filterNot { paramsMap.containsKey(it.name) }
			.map { it.name }
			.fold("") { acc, name -> acc + "Parameter $name is required.\n" }

		if (missingParamsMessage.isNotEmpty()) {
			throw RuntimeException(missingParamsMessage)
		}

		val kParamsKeys = kParams.map { it.name }

		val unknownParamsMessage = paramsMap.filterNot { entry -> kParams.any { entry.key == it.name  } }
			.map { it.key }
			.fold("") { acc, name -> acc + "Unknown parameter $name.\n" }

		if (unknownParamsMessage.isNotEmpty()) {
			throw RuntimeException(unknownParamsMessage)
		}
	}

	private fun convertString(kParameter: KParameter, value: String?): Any? {
		logger.debug { "Calling convertString kParameter:$kParameter, kParameter.type: ${kParameter.type} $value" }

		return if (value != null) {
			when (kParameter.type.toString()) {
				"String" -> value
				"Int" -> value.toInt()
				"Long" -> value.toLong()
				"Float" -> value.toFloat()
				"Double" -> value.toDouble()
				else -> throw RuntimeException("$value does not match expected type ${kParameter.type}.")
			}
		} else {
			null
		}
	}

	private fun <T : TransferParameters> buildTransferParameters(paramsMap: Map<String, String>, cls: KClass<T>): T {
		if (cls.constructors.size > 1) {
			throw RuntimeException("TransferParamters can only contain one constructor.")
		}

		val constructor = cls.constructors.first()

		val kParams = constructor.parameters

		validateParameters(paramsMap, kParams)

		val kParamsMap = kParams.filterNot { it.isOptional && paramsMap[it.name] == null }
			.associate { it to convertString(it, paramsMap[it.name]) }

		return constructor.callBy(kParamsMap)
	}

	override fun <T : TransferParameters> parse(direction: Direction, parameters: String): T {

		val map = parameters.split(",")
			.map { (it.split("=", limit=2)) }
			.associate { (it[0].trim() to it[1].trim()) }

		val type = map["type"]

		val className = when(direction) {
			Direction.SOURCE ->	when(type) {
				"file" -> "${BASE_PACKAGE}.FileSourceParameters"
				else -> ""
			}
			Direction.DESTINATION -> when(type) {
				"file" -> "${BASE_PACKAGE}.FileDestinationParameters"
				else -> ""
			}
		}

		if (className.isEmpty()) {
			throw RuntimeException("$type type is unknown.")
		}

		return buildTransferParameters(map, Class.forName(className).kotlin as KClass<T>)
	}
}