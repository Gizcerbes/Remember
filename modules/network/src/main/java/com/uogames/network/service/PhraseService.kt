package com.uogames.network.service

import com.uogames.network.response.PhraseResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PhraseService(private val client: HttpClient) {

	suspend fun get(like: String): PhraseResponse = client.get("/phrase") {
		parameter("like", like)
	}.body()

	suspend fun get(globalId: Long): PhraseResponse = client.get("/phrase/$globalId").body()

	suspend fun post(phrase: PhraseResponse): PhraseResponse = client.post("/phrase"){
		contentType(ContentType.Application.Json)
		setBody(phrase)
	}.body()

	suspend fun put(phrase: PhraseResponse): PhraseResponse = client.put("/phrase"){
		contentType(ContentType.Application.Json)
		setBody(phrase)
	}.body()


}