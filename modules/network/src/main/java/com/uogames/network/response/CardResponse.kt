package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class CardResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("id_phrase")
	val idPhrase: Long = 0,
	@SerializedName("id_translate")
	val idTranslate: Long = 0,
	@SerializedName("id_image")
	val idImage: Long? = null,
	@SerializedName("reason")
	val reason: String = "",
	@SerializedName("time_change")
	val timeChange: Long = 0,
	@SerializedName("like")
	val like: Long = 0,
	@SerializedName("dislike")
	val dislike: Long = 0,
	@SerializedName("global_id")
	val globalId: Long? = null,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)
