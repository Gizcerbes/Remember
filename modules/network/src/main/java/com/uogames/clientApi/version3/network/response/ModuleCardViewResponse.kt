package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class ModuleCardViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("user")
    var user: UserViewResponse,
    @SerializedName("module")
    var module: ModuleViewResponse,
    @SerializedName("card")
    var idCard: CardViewResponse
)