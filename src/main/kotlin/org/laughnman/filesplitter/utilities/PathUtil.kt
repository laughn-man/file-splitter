package org.laughnman.filesplitter.utilities

/**
 * Removes slashes at the beginning and end of the path string.
 */
fun String.normalizePath(): String {
	val normalizedPath = if (this.endsWith("/")) this.dropLast(1) else this
	return if (normalizedPath.startsWith("/")) normalizedPath.drop(1) else normalizedPath
}

/**
 * Gets the file name from a given path.
 */
fun String.findFileName(): String {
	val index = this.lastIndexOf("/")
	return if (index == -1) this else this.substring(index + 1)
}