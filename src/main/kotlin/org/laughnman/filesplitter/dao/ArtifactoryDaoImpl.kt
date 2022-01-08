package org.laughnman.filesplitter.dao

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.utils.io.*
import org.laughnman.filesplitter.models.artifactory.FileInfo
import org.laughnman.filesplitter.models.artifactory.FolderInfo
import org.laughnman.filesplitter.utilities.base64Encode
import org.laughnman.filesplitter.utilities.exceptions.ArtifactoryInputException
import org.laughnman.filesplitter.utilities.normalizePath
import org.laughnman.filesplitter.utilities.sha256Hash
import org.laughnman.filesplitter.utilities.toHex
import java.net.URI
import java.nio.file.Path

class ArtifactoryDaoImpl(private val client: HttpClient) : ArtifactoryDao {

	private fun buildAuthHeader(user: String, password: String, token: String) = if (user.isNotEmpty() && token.isNotEmpty()) {
		"Basic " + "$user:$token".toByteArray().base64Encode()
	}
	else if (user.isNotEmpty() && password.isNotEmpty()) {
		"Basic " + "$user:$password".toByteArray().base64Encode()
	}
	else if (token.isNotEmpty()) {
		"Bearer $token"
	}
	else {
		throw ArtifactoryInputException("Both User and Token were empty.")
	}

	override suspend fun getFileInfo(url: URI, filePath: String, user: String, password: String, token: String): FileInfo {

		val normalizedUrl = url.toString().normalizePath()
		val normalizedFilePath = filePath.normalizePath()

		return client.get("$normalizedUrl/api/storage/$normalizedFilePath") {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
			}
		}
	}

	override suspend fun getFolderInfo(url: URI, filePath: String, user: String, password: String, token: String): FolderInfo {
		val normalizedUrl = url.toString().normalizePath()
		val normalizedFilePath = filePath.normalizePath()

		return client.get("$normalizedUrl/api/storage/$normalizedFilePath") {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
			}
		}
	}

	override suspend fun downloadArtifact(url: URI, filePath: String, user: String, password: String, token: String, f: suspend (channel: ByteReadChannel) -> Unit) {
		val normalizedUrl = url.toString().normalizePath()
		val normalizedFilePath = filePath.normalizePath()

		client.get<HttpStatement>("$normalizedUrl/$normalizedFilePath") {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
			}
		}.execute {
			f(it.receive<ByteReadChannel>())
		}
	}

	override suspend fun deployArtifact(url: URI, filePath: String, input: ByteArray, user: String, password: String, token: String): FileInfo {
		val normalizedUrl = url.toString().normalizePath()
		val normalizedFilePath = filePath.normalizePath()

		return client.put("$normalizedUrl/$normalizedFilePath") {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
				append("X-Checksum-Sha256", input.sha256Hash().toHex())
			}

			body = ByteArrayContent(input)
		}
	}

	override suspend fun deployArtifactWithChecksum(url: URI, filePath: String, input: ByteArray, user: String,	password: String,	token: String): FileInfo {
		val normalizedUrl = url.toString().normalizePath()
		val normalizedFilePath = filePath.normalizePath()

		return client.put("$normalizedUrl/$normalizedFilePath") {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
				append("X-Checksum-Deploy", "true")
				append("X-Checksum-Sha256", input.sha256Hash().toHex())
			}
		}
	}
}