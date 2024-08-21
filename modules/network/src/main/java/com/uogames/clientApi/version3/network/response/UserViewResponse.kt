package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserViewResponse(
    @SerializedName("global_owner")
    val globalOwner: String,
    @SerializedName("name")
    val name: String = "",
)