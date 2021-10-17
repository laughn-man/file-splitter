package org.laughnman.filesplitter.services

import org.koin.dsl.module

val servicesModule = module {
	single { StartupServiceImpl(get()) as StartupService }
	single { FileSplitterServiceImpl() as FileSplitterService }
}