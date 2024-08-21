package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class PhraseViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var globalOwner: UserViewResponse,
    @SerializedName("phrase")
    var phrase: String = "",
    @SerializedName("definition")
    var definition: String? = null,
    @SerializedName("lang")
    var lang: String = "eng",
    @SerializedName("country")
    var country: String = "BELARUS",
    @SerializedName("pronounce")
    var pronounce: PronunciationViewResponse? = null,
    @SerializedName("image")
    var image: ImageViewResponse? = null,
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
)