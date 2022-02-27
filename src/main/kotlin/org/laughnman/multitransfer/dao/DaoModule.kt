package org.laughnman.multitransfer.dao

import org.koin.dsl.module

val daoModule = module {
	single<FileDao> { FileDaoImpl() }
	single { ArtifactoryDao.Factory }
}