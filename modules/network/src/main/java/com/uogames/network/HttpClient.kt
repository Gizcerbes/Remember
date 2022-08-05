package com.uogames.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

object HttpClient {

	private val client = HttpClient(OkHttp){
		install(DefaultRequest)
		defaultRequest {
			url("http://93.125.42.151:8080")
		}
		engine {
			config {
				followRedirects(false)
			}
		}
		install(ContentNegotiation) {
			gson()
		}
	}



}