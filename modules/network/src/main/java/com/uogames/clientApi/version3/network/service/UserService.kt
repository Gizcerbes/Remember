package com.uogames.clientApi.version3.network.service


import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*

class UserService(private val client: HttpClient) {

	suspend fun get(globalOwner: String): UserResponse = client
		.get("/remember-card/v3/user/info/$globalOwner")
		.ifSuccess()

	suspend fun getView(globalOwner: String): UserResponse = client
		.get("/remember-card/v3/user/info/view/$globalOwner")
		.ifSuccess()

}