package com.uogames.clientApi.version3.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class PronunciationResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String = "",
	@SerializedName("audio_uri")
	var audioUri: String
)