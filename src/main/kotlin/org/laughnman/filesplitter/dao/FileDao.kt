package org.laughnman.filesplitter.dao

import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface FileDao {

	fun getLength(file: File): Long

	fun isFile(file: File): Boolean

	fun isDirectory(file: File): Boolean

	fun delete(file: File): Boolean

	fun openForRead(file: File): InputStream

	fun openForWrite(file: File): OutputStream

}