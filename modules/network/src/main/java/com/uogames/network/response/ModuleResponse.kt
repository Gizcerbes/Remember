package com.uogames.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class ModuleResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("name")
	var name: String = "",
	@SerializedName("time_change")
	var timeChange: Long = 0,
	@SerializedName("like")
	var like: Long = 0,
	@SerializedName("dislike")
	var dislike: Long = 0,

)
