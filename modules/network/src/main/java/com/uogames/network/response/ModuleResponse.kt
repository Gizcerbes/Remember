package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class ModuleResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("name")
	val name: String = "",
	@SerializedName("owner")
	val owner: String = "",
	@SerializedName("time_change")
	val timeChange: Long = 0,
	@SerializedName("like")
	val like: Long = 0,
	@SerializedName("dislike")
	val dislike: Long = 0,
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)
