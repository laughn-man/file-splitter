package org.laughnman.filesplitter.dao

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import org.koin.dsl.module
import org.laughnman.filesplitter.services.*

val daoModule = module {
	single {
		HttpClient() {
			install(JsonFeature) {
				serializer = JacksonSerializer() {
					configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				}
			}

			install(HttpTimeout) {
				requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
			}
		}
	}
	single<FileDao> { FileDaoImpl() }
	single<ArtifactoryDao> { ArtifactoryDaoImpl(get()) }
}