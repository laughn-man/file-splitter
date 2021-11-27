package org.laughnman.filesplitter.dao

import java.io.File
import java.io.InputStream

private const val BUFFER_SIZE = 8192

class FileDaoImpl : FileDao {

	override fun getLength(file: File) = file.length()

	override fun isFile(file: File) = file.isFile

	override fun isDirectory(file: File) = file.isDirectory

	override fun delete(file: File) = file.delete()

	override fun openForRead(file: File) = file.inputStream()

	override fun openForWrite(file: File) = file.outputStream()
}