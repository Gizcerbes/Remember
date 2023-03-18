package com.uogames.clientApi.version3.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class CardViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserViewResponse,
    @SerializedName("phrase")
    var phrase: PhraseViewResponse,
    @SerializedName("translate")
    var translate: PhraseViewResponse,
    @SerializedName("image")
    var image: ImageViewResponse? = null,
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
)