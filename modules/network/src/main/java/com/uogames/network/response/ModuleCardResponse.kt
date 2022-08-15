package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class ModuleCardResponse(
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("module_id")
	var idModule: Long,
	@SerializedName("card_id")
	var idCard: Long
)