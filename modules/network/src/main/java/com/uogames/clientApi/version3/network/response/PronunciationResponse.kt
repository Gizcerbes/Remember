package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class PronunciationResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String = "",
	@SerializedName("audio_uri")
	var audioUri: String
)