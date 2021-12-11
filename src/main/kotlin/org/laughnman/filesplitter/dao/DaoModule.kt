package org.laughnman.filesplitter.dao

import io.ktor.client.*
import org.koin.dsl.module
import org.laughnman.filesplitter.services.*

val daoModule = module {
	single { HttpClient() }
	single { FileDaoImpl() as FileDao }
	single { ArtifactoryDaoImpl(get()) as ArtifactoryDao }
}