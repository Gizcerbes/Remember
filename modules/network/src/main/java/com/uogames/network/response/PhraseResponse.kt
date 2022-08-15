package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class PhraseResponse(
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	var globalOwner: String,
	@SerializedName("phrase")
	var phrase: String = "",
	@SerializedName("definition")
	var definition: String? = null,
	@SerializedName("lang")
	var lang: String = "eng-gb",
	@SerializedName("id_pronounce")
	var idPronounce: Long? = null,
	@SerializedName("id_image")
	var idImage: Long? = null,
	@SerializedName("time_change")
	var timeChange: Long = 0,
	@SerializedName("like")
	var like: Long = 0,
	@SerializedName("dislike")
	var dislike: Long = 0
)