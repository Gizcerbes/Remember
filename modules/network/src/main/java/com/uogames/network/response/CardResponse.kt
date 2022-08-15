package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class CardResponse(
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String,
	@SerializedName("id_phrase")
	var idPhrase: Long = 0,
	@SerializedName("id_translate")
	var idTranslate: Long = 0,
	@SerializedName("id_image")
	var idImage: Long? = null,
	@SerializedName("reason")
	var reason: String = "",
	@SerializedName("time_change")
	var timeChange: Long = 0,
	@SerializedName("like")
	var like: Long = 0,
	@SerializedName("dislike")
	var dislike: Long = 0,

)
