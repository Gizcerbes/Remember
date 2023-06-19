package com.uogames.clientApi.version3.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class ModuleViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserViewResponse,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("time_change")
    var timeChange: Long = 0,
    @SerializedName("like")
    var like: Long = 0,
    @SerializedName("dislike")
    var dislike: Long = 0
)