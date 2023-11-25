package org.laughnman.multitransfer.services

import org.koin.dsl.module
import org.laughnman.multitransfer.services.transfer.TransferFactoryService
import org.laughnman.multitransfer.services.transfer.TransferFactoryServiceImpl
import org.laughnman.multitransfer.services.transfer.TransferService
import org.laughnman.multitransfer.services.transfer.TransferServiceImpl

val servicesModule = module {
	single<StartupService> { StartupServiceImpl(get(), get()) }
	single<FileSplitterService> { FileSplitterServiceImpl(get()) }
	single<TransferFactoryService> { TransferFactoryServiceImpl(get(), get(), get()) }
	single<TransferService> { TransferServiceImpl(get()) }
}