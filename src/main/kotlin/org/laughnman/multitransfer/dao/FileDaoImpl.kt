package org.laughnman.multitransfer.dao

import java.io.File

class FileDaoImpl : FileDao {

	override fun getLength(file: File) = file.length()

	override fun isFile(file: File) = file.isFile

	override fun isDirectory(file: File) = file.isDirectory

	override fun delete(file: File) = file.delete()

	override fun openForRead(file: File) = file.inputStream()

	override fun openForWrite(file: File) = file.outputStream()

	override fun openReadChannel(file: File) = file.inputStream().channel

	override fun openWriteChannel(file: File) = file.outputStream().channel
}