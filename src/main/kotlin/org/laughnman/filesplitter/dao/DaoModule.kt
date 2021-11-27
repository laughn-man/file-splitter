package org.laughnman.filesplitter.dao

import org.koin.dsl.module
import org.laughnman.filesplitter.services.*

val daoModule = module {
	single { FileDaoImpl() as FileDao }
}