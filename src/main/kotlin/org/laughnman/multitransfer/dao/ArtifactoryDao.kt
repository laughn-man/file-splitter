package org.laughnman.multitransfer.dao

import io.ktor.utils.io.*
import org.laughnman.multitransfer.models.artifactory.FileInfo
import org.laughnman.multitransfer.models.artifactory.FolderInfo
import java.net.URI

interface ArtifactoryDao {

	suspend fun getFileInfo(url: URI, filePath: String, user: String = "", password: String = "", token: String = ""): FileInfo

	suspend fun getFolderInfo(url: URI, filePath: String, user: String = "", password: String = "", token: String = ""): FolderInfo

	suspend fun downloadArtifact(url: URI, filePath: String, user: String = "", password: String = "", token: String = "", f: suspend (channel: ByteReadChannel) -> Unit)

	suspend fun deployArtifact(url: URI, filePath: String, input: ByteArray, user: String = "", password: String = "", token: String = ""): FileInfo

	suspend fun deployArtifactWithChecksum(url: URI, filePath: String, input: ByteArray, user: String = "", password: String = "", token: String = ""): FileInfo

}