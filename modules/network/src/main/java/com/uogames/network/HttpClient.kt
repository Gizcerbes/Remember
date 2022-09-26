package com.uogames.network

import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class HttpClient(
	private val secret: () -> String,
	private val data: () -> Map<String, String>,
	val defaultUrl: () -> String,
	keystoreInput: ByteArray? = null,
	keystorePassword: CharArray? = null
) {

	private val ssl = getSsl(keystoreInput, keystorePassword)
	private var picasso: Picasso? = null

	private val okHttpClient = OkHttpClient.Builder().apply {
		connectTimeout(10, TimeUnit.SECONDS)
		followRedirects(false)
		connectionPool(ConnectionPool(16, 5, TimeUnit.SECONDS))
		setSsl(ssl)
	}.build()

	private val client = HttpClient(OkHttp) {
		engine {
			preconfigured = okHttpClient
			threadsCount = 16
		}
		install(DefaultRequest) {
			bearerAuth(JWTBuilder.create(secret(), data(), 10000))
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
			sslSocketFactory(ssl.getSslContext().socketFactory, ssl.getTrustManager())
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

	fun getPicasso(context: Context): Picasso {
		if (picasso == null) synchronized(this) {
			if (picasso == null){
				picasso = Picasso
					.Builder(context)
					.downloader(OkHttp3Downloader(okHttpClient))
					.build()
			}
		}
		return picasso as Picasso
	}

	suspend fun get(url: URLBuilder, builder: HttpRequestBuilder.() -> Unit = {}) = client.get(urlBuilder(url), builder)

	suspend fun get(url: String, builder: HttpRequestBuilder.() -> Unit = {}) = get(URLBuilder(url), builder)

	suspend fun post(url: URLBuilder, builder: HttpRequestBuilder.() -> Unit = {}) = client.post(urlBuilder(url), builder)

	suspend fun post(url: String, builder: HttpRequestBuilder.() -> Unit = {}) = post(URLBuilder(url), builder)

	suspend fun head(url: URLBuilder, builder: HttpRequestBuilder.() -> Unit = {}) = client.head(urlBuilder(url), builder)

	suspend fun head(url: String, builder: HttpRequestBuilder.() -> Unit = {}) = head(URLBuilder(url), builder)

}