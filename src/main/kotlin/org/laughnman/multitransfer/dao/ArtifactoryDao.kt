package org.laughnman.multitransfer.dao

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.utils.io.*
import mu.KotlinLogging
import org.laughnman.multitransfer.models.artifactory.FileInfo
import org.laughnman.multitransfer.models.artifactory.FolderInfo
import org.laughnman.multitransfer.models.transfer.AbstractArtifactoryCommand
import java.net.URI
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

private val logger = KotlinLogging.logger {}

interface ArtifactoryDao {

	companion object Factory {

		fun fromCommand(artifactoryCommand: AbstractArtifactoryCommand): ArtifactoryDao {
			val httpClient = HttpClient(CIO) {
				expectSuccess = true
				engine {
					https {
						// Turn off the trust manager if the site is insecure.
						if (artifactoryCommand.insecure) {
							logger.warn { "Connecting to Artifactory in insecure mode." }
							trustManager = object : X509TrustManager {
								override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

								override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

								override fun getAcceptedIssuers(): Array<X509Certificate>? = null
							}
						}
					}
				}

				install(ContentNegotiation) {
					jackson {
						configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					}
				}

				install(HttpTimeout) {
					requestTimeoutMillis = artifactoryCommand.requestTimeout
				}
			}

			return ArtifactoryDaoImpl(httpClient)
		}
	}

	suspend fun getFileInfo(url: URI, filePath: String, user: String = "", password: String = "", token: String = ""): FileInfo

	suspend fun getFolderInfo(url: URI, filePath: String, user: String = "", password: String = "", token: String = ""): FolderInfo

	suspend fun downloadArtifact(url: URI, filePath: String, user: String = "", password: String = "", token: String = "", f: suspend (channel: ByteReadChannel) -> Unit)

	suspend fun deployArtifact(url: URI, filePath: String, input: ByteArray, user: String = "", password: String = "", token: String = ""): FileInfo

	suspend fun deployArtifactWithChecksum(url: URI, filePath: String, input: ByteArray, user: String = "", password: String = "", token: String = ""): FileInfo

}