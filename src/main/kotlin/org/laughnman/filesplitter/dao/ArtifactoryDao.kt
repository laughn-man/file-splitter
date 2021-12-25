package org.laughnman.filesplitter.dao

import io.ktor.utils.io.*
import org.laughnman.filesplitter.models.artifactory.FileInfo
import java.net.URI
import java.nio.file.Path

interface ArtifactoryDao {

	suspend fun getFileInfo(url: URI, path: Path, user: String = "", password: String = "", token: String = ""): FileInfo

	suspend fun downloadArtifact(url: URI, path: Path, user: String = "", password: String = "", token: String = "", f: suspend (channel: ByteReadChannel) -> Unit)

	suspend fun deployArtifact(url: URI, path: Path, input: ByteArray, user: String = "", password: String = "", token: String = ""): FileInfo

}