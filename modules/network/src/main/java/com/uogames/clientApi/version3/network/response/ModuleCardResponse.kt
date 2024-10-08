package com.uogames.clientApi.version3.network.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class ModuleCardResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	var globalOwner: String = "",
	@SerializedName("module_id")
	var idModule: UUID,
	@SerializedName("card_id")
	var idCard: UUID
)