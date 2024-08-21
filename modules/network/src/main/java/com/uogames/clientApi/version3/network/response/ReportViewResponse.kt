package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class ReportViewResponse(
    @SerializedName("global_id")
    val globalId: UUID,
    @SerializedName("claimant")
    var claimant: UserViewResponse,
    @SerializedName("message")
    var message: String,
    @SerializedName("accused")
    var accused: UserViewResponse,
    @SerializedName("phrase")
    var idPhrase: PhraseViewResponse? = null,
    @SerializedName("card")
    var idCard: CardViewResponse? = null,
    @SerializedName("module")
    var idModule: ModuleViewResponse? = null
)