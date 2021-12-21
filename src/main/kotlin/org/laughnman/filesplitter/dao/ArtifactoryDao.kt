package org.laughnman.filesplitter.dao

import io.ktor.client.statement.*
import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.io.InputStream

interface ArtifactoryDao {

	suspend fun download(url: String, user: String = "", password: String = "", token: String = "", block: suspend (response: HttpResponse) -> Unit)

	suspend fun upload(url: String, input: ByteArray, user: String = "", password: String = "", token: String = "")

}