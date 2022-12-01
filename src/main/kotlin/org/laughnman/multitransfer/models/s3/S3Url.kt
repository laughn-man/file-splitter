package org.laughnman.multitransfer.models.s3

import picocli.CommandLine.ITypeConverter
import java.lang.IllegalArgumentException

private val regex = Regex("s3://([a-z0-9\\.-]+)/([\\w!-_\\.*'\\(\\)]+)")

fun String.toS3Url(): S3Url {
	val match = regex.matchEntire(this) ?: throw IllegalArgumentException("Value $this is not a valid S3 URL.")

	val (b, k) = match.destructured
	return S3Url(b, k)
}

data class S3Url(val bucket: String, val key: String) {
	override fun toString(): String = "s3://$bucket/$key"
}

class S3UrlConverter : ITypeConverter<S3Url> {
	override fun convert(value: String): S3Url = value.toS3Url()
}