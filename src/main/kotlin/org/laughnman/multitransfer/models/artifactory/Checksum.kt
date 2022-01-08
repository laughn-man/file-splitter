package org.laughnman.multitransfer.models.artifactory

data class Checksum (
	val md5: String,
	val sha1: String,
	val sha256: String
	)