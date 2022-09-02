package com.uogames.network.service

import com.uogames.network.ifSuccess
import com.uogames.network.response.PhraseResponse
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient
import java.util.*

class PhraseService(private val client: HttpClient) {

	suspend fun count(like: String): Long = client.get("/phrase/count"){
		parameter("like", like)
	}.ifSuccess()

	suspend fun get(like: String, number: Long): PhraseResponse = client.get("/phrase") {
		parameter("like", like)
		parameter("number", number)
	}.ifSuccess()

	suspend fun get(globalId: UUID): PhraseResponse = client.get("/phrase/$globalId").ifSuccess()

	suspend fun post(phrase: PhraseResponse): PhraseResponse = client.post("/phrase") {
		contentType(ContentType.Application.Json)
		setBody(phrase)
	}.ifSuccess()

}