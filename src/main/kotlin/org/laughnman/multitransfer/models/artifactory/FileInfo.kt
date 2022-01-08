package org.laughnman.multitransfer.models.artifactory

data class FileInfo (
	val downloadUri: String,
	val repo: String,
	val path: String,
	val created: String,
	val createdBy: String,
	val size: Long,
	val mimeType: String,
	val checksums: Checksum
	)