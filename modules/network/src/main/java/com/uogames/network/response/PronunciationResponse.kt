package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class PronunciationResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("audio_uri")
	val audioUri: String,
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)