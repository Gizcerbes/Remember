package com.uogames.network.service

import com.uogames.network.ifSuccess
import com.uogames.network.response.CardResponse
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient
import java.util.*

class CardService(private val client: HttpClient) {

	suspend fun get(like:String, number: Long): CardResponse = client.get("/card"){
		parameter("like", like)
		parameter("number", number)
	}.ifSuccess()

	suspend fun get(globalId: UUID): CardResponse = client.get("/card/$globalId").ifSuccess()

	suspend fun count(like: String): Long = client.get("/card/count"){
		parameter("like", like)
	}.ifSuccess()

	suspend fun post(card: CardResponse): CardResponse = client.post("/card"){
		contentType(ContentType.Application.Json)
		setBody(card)
	}.ifSuccess()

}