package org.laughnman.filesplitter.utilities

import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.*

private const val BUFFER_SIZE = 8192

fun InputStream.readAsSequence(): Sequence<Pair<Int, ByteArray>> {
	val buffer = ByteArray(BUFFER_SIZE)

	return generateSequence {
		val count = this.read(buffer, 0, BUFFER_SIZE)
		if (count > -1) Pair(count, buffer)	else null
	}
}

fun ByteArray.base64Encode(): String = Base64.getEncoder().encodeToString(this)

fun File.sha256Hash(): String {
	val digest = MessageDigest.getInstance("SHA-256")

	this.inputStream().use { fin ->
		fin.readAsSequence().forEach { (count, buffer) ->
			digest.update(buffer, 0, count)
		}
	}

	return digest.digest().base64Encode()
}