package org.laughnman.multitransfer.models.artifactory

data class FolderInfo(
	val uri: String,
	val repo: String,
	val path: String,
	val created: String,
	val createdBy: String,
	val children: List<FolderChild>
)
