package com.uogames.network.service

import com.uogames.network.response.ModuleResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ModuleService(private val client: HttpClient) {

	suspend fun get(like:String):ModuleResponse = client.get("/module"){
		parameter("like", like)
	}.body()

	suspend fun get(globalId: Long): ModuleResponse = client.get("/module/$globalId").body()

	suspend fun post(module: ModuleResponse): ModuleResponse = client.post("/module"){
		contentType(ContentType.Application.Json)
		setBody(module)
	}.body()

	suspend fun put(module:ModuleResponse): ModuleResponse = client.put("/module"){
		contentType(ContentType.Application.Json)
		setBody(module)
	}.body()

}