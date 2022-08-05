package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class ModuleCardResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("module_id")
	val idModule: Long,
	@SerializedName("card_id")
	val idCard: Long,
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)