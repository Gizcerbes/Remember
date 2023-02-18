package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.UserResponse
import com.uogames.dto.global.GlobalUser

object UserMap: Map<UserResponse, GlobalUser> {
	override fun UserResponse.toDTO() = GlobalUser(
		globalOwner = globalOwner,
		name = name
	)

	override fun GlobalUser.toResponse() = UserResponse(
		globalOwner = globalOwner,
		name = name
	)
}