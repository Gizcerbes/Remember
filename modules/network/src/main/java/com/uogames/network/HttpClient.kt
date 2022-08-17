package com.uogames.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import okhttp3.OkHttpClient

class HttpClient(
	private val secret: () -> String,
	private val data: () -> Map<String, String>,
	val defaultUrl: () -> String,
	keystoreInput: ByteArray? = null,
	keystorePassword: CharArray? = null
) {
	private val ssl = getSsl(keystoreInput, keystorePassword)
	private val okHttpClient = OkHttpClient.Builder().build()
	val client = HttpClient(OkHttp) {
		engine {
			config {
				followRedirects(false)
				setSsl(ssl)
			}
			preconfigured = okHttpClient
		}
		install(DefaultRequest) {
			bearerAuth(JWTBuilder.create(secret(), data()))
			//url(defaultUrl())
		}
		install(ContentNegotiation) {
			gson()
		}

		if (BuildConfig.DEBUG) install(Logging) {
			logger = Logger.SIMPLE
			level = LogLevel.ALL
		}
	}

	private fun getSsl(keystoreInput: ByteArray?, keystorePassword: CharArray?): SslSettings? {
		return if (keystoreInput != null && keystorePassword != null) {
			SslSettings(keystoreInput, keystorePassword)
		} else {
			null
		}
	}

	private fun OkHttpClient.Builder.setSsl(ssl: SslSettings?) {
		if (ssl != null) {
			sslSocketFactory(ssl.getSslContext()!!.socketFactory, ssl.getTrustManager())
			hostnameVerifier { _, _ -> true }
		}
	}

	private fun urlBuilder(url: URLBuilder): String {
		val defUrl = URLBuilder(defaultUrl()).build()
		return if (url.host.isEmpty()) {
			defUrl.toString() + url.encodedPath
		} else {
			url.build().toString()
		}
	}

	suspend fun get(url: URLBuilder, builder: HttpRequestBuilder.() -> Unit = {}) = client.get(urlBuilder(url), builder)

	suspend fun get(url: String, builder: HttpRequestBuilder.() -> Unit = {}) = get(URLBuilder(url), builder)

	suspend fun post(url: URLBuilder, builder: HttpRequestBuilder.() -> Unit = {}) =
		client.post(urlBuilder(url), builder)

	suspend fun post(url: String, builder: HttpRequestBuilder.() -> Unit = {}) = post(URLBuilder(url), builder)


}