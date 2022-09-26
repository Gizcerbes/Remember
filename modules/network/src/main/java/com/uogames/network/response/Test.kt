package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class Test(
	@SerializedName("audio_uri")
	val audioUri: String?,
	@SerializedName("global_id")
	val globalId: String?,
	@SerializedName("global_owner")
	val globalOwner: String?
)