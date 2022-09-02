package com.uogames.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class ModuleCardResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("module_id")
	var idModule: UUID,
	@SerializedName("card_id")
	var idCard: UUID
)