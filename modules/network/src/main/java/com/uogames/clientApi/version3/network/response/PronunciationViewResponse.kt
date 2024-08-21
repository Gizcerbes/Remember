package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class PronunciationViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    val user: UserViewResponse,
    @SerializedName("audio_uri")
    var audioUri: String
)