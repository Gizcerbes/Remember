package com.uogames.network.service

import com.uogames.network.response.CardResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class CardService(private val client: HttpClient) {

	suspend fun get(like:String): CardResponse = client.get("/card"){
		parameter("like", like)
	}.body()

	suspend fun get(globalId: Long): CardResponse = client.get("/card/$globalId").body()

	suspend fun post(card: CardResponse): CardResponse = client.post("/card"){
		contentType(ContentType.Application.Json)
		setBody(card)
	}.body()

	suspend fun put(card: CardResponse): CardResponse = client.put("/card"){
		contentType(ContentType.Application.Json)
		setBody(card)
	}.body()

}