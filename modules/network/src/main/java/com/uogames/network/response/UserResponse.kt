package com.uogames.network.response

import com.google.gson.annotations.SerializedName


data class UserResponse(
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("name")
	val name: String
)