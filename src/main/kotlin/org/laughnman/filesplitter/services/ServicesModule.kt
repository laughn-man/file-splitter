package org.laughnman.filesplitter.services

import org.koin.dsl.module

val servicesModule = module {
	single<StartupService> { StartupServiceImpl(get(), get()) }
	single<FileSplitterService> { FileSplitterServiceImpl(get()) }
	single<TransferFactoryService> { TransferFactoryServiceImpl(get(), get()) }
}