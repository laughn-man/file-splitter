package org.laughnman.multitransfer.utilities

import mu.KotlinLogging
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

private val logger = KotlinLogging.logger {}

class HttpsUtil {

	private class AllTrustManager : X509TrustManager {
		override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

		override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

		override fun getAcceptedIssuers(): Array<X509Certificate>? {
			return null
		}
	}

	private class AllHostsVerifier : HostnameVerifier {
		override fun verify(hostname: String?, session: SSLSession?): Boolean {
			return true
		}
	}

	fun turnOnInsecureSsl() {

		logger.warn { "Running in insecure SSL mode." }

		val trustManagers = arrayOf(AllTrustManager())

		val sc = SSLContext.getInstance("SSL")
		sc.init(null, trustManagers, SecureRandom())
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

		val allHostsVerifier = AllHostsVerifier()
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsVerifier)
	}

	fun overrideTrustStore(trustStore: String, password: String) {
		System.setProperty("javax.net.ssl.trustStore", trustStore)
		System.setProperty("javax.net.ssl.trustStorePassword", password)
	}

}