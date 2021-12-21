package org.laughnman.filesplitter.dao

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.laughnman.filesplitter.models.transfer.TransferInfo
import org.laughnman.filesplitter.utilities.base64Encode
import org.laughnman.filesplitter.utilities.exceptions.ArtifactoryInputException
import org.laughnman.filesplitter.utilities.sha256Hash

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

	override suspend fun download(url: String, user: String, password: String, token: String, block: suspend (response: HttpResponse) -> Unit) {
		client.get<HttpStatement>(url) {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
			}
		}.execute(block)
	}

	override suspend fun upload(url: String, input: ByteArray, user: String, password: String, token: String) {

		client.put<Unit>(url) {
			headers {
				append(HttpHeaders.Authorization, buildAuthHeader(user, password, token))
			}

			body = ByteArrayContent(input)
		}
	}
}