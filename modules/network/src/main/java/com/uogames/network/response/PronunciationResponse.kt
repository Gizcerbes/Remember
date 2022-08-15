package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class PronunciationResponse(
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("audio_uri")
	val audioUri: String
)