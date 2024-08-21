package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class ReportResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("claimant")
    var claimant: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("accused")
    var accused: String,
    @SerializedName("id_phrase")
    var idPhrase: UUID? = null,
    @SerializedName("id_card")
    var idCard: UUID? = null,
    @SerializedName("id_module")
    var idModule: UUID? = null
)