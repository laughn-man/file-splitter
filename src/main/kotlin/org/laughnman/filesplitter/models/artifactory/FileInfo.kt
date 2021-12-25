package org.laughnman.filesplitter.models.artifactory

data class FileInfo (
	val uri: String,
	val downloadUri: String,
	val repo: String,
	val path: String,
	val remoteUrl: String,
	val created: String,
	val createdBy: String,
	val lastModified: String,
	val modifiedBy: String,
	val lastUpdated: String,
	val size: Long,
	val mimeType: String,
	val checksums: Checksum,
	val originalChecksums: Checksum
	)