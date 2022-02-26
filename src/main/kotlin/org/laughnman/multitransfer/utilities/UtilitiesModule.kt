package org.laughnman.multitransfer.utilities

import org.koin.dsl.module

val utilitiesModule = module {
	single { HttpsUtil() }
}