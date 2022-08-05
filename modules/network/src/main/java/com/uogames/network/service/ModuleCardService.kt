package com.uogames.network.service

import com.uogames.network.response.ModuleCardResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ModuleCardService(private val client: HttpClient) {

	suspend fun get(like: String): ModuleCardService = client.get("/module/card") {
		parameter("like", like)
	}.body()

	suspend fun get(globalId: Long): ModuleCardService = client.get("/module/card/$globalId").body()

	suspend fun post(moduleCard: ModuleCardResponse): ModuleCardResponse = client.post("/module/card"){
		contentType(ContentType.Application.Json)
		setBody(moduleCard)
	}.body()

	suspend fun put(moduleCard: ModuleCardResponse): ModuleCardResponse =client.put("/module/card"){
		contentType(ContentType.Application.Json)
		setBody(moduleCard)
	}.body()

}