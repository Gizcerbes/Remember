package com.uogames.network.service

import com.uogames.network.ifSuccess
import com.uogames.network.response.ModuleResponse
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient
import java.util.*

class ModuleService(private val client: HttpClient) {

	suspend fun get(like: String, number: Long): ModuleResponse = client.get("/module") {
		parameter("like", like)
		parameter("number", number)
	}.ifSuccess()

	suspend fun count(like: String): Long = client.get("/module/count"){
		parameter("like", like)
	}.ifSuccess()

	suspend fun get(globalId: UUID): ModuleResponse = client.get("/module/$globalId").ifSuccess()

	suspend fun post(module: ModuleResponse): ModuleResponse = client.post("/module") {
		contentType(ContentType.Application.Json)
		setBody(module)
	}.ifSuccess()

}