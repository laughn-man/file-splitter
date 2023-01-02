package org.laughnman.multitransfer.models.transfer

sealed interface Transfer

object Complete : Transfer

data class Start(val metaInfo: MetaInfo) : Transfer

data class Error(val exception: Exception) : Transfer

object BufferReady : Transfer

