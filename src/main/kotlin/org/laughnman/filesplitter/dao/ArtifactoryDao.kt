package org.laughnman.filesplitter.dao

import org.laughnman.filesplitter.models.transfer.TransferInfo
import java.io.InputStream

interface ArtifactoryDao {

	suspend fun download(url: String, input: ByteArray, user: String = "", password: String = "", token: String = "")

	suspend fun upload(url: String, input: ByteArray, user: String = "", password: String = "", token: String = "")

}