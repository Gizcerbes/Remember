package com.uogames.network.map

import com.uogames.dto.User
import com.uogames.network.response.UserResponse

object UserMap: Map<UserResponse, User> {
	override fun UserResponse.toDTO() = User(
		globalOwner = globalOwner,
		name = name
	)

	override fun User.toResponse() = UserResponse(
		globalOwner = globalOwner,
		name = name
	)
}