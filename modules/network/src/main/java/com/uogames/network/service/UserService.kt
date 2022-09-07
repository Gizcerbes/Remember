package com.uogames.network.service

import com.uogames.network.HttpClient
import com.uogames.network.ifSuccess
import com.uogames.network.response.UserResponse

class UserService(private val client: HttpClient) {

	suspend fun get(globalOwner: String): UserResponse = client.get("/user/info/$globalOwner").ifSuccess()

}