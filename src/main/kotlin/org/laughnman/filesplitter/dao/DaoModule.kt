package org.laughnman.filesplitter.dao

import io.ktor.client.*
import io.ktor.client.features.json.*
import org.koin.dsl.module
import org.laughnman.filesplitter.services.*

val daoModule = module {
	single {
		HttpClient() {
			install(JsonFeature)
		}
	}
	single { FileDaoImpl() as FileDao }
	single { ArtifactoryDaoImpl(get()) as ArtifactoryDao }
}