package com.uogames.network.service

import com.uogames.network.ifSuccess
import com.uogames.network.response.ModuleCardResponse
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient

class ModuleCardService(private val client: HttpClient) {

	suspend fun count(moduleID: Long): Long = client.get("/module-card/count/$moduleID").ifSuccess()

	suspend fun get(moduleID: Long, number: Long): ModuleCardResponse = client.get("/module-card") {
		parameter("moduleID", moduleID)
		parameter("number", number)
	}.ifSuccess()

	suspend fun get(globalId: Long): ModuleCardResponse = client.get("/module-card/$globalId").ifSuccess()

	suspend fun post(moduleCard: ModuleCardResponse): ModuleCardResponse = client.post("/module-card"){
		contentType(ContentType.Application.Json)
		setBody(moduleCard)
	}.ifSuccess()


}