package com.uogames.network.response

import com.google.gson.annotations.SerializedName

data class PhraseResponse(
	@SerializedName("id")
	val id: Long = 0,
	@SerializedName("phrase")
	val phrase: String = "",
	@SerializedName("definition")
	val definition: String? = null,
	@SerializedName("lang")
	val lang: String = "eng-gb",
	@SerializedName("id_pronounce")
	val idPronounce: Long? = null,
	@SerializedName("id_image")
	val idImage: Long? = null,
	@SerializedName("time_change")
	val timeChange: Long = 0,
	@SerializedName("like")
	val like: Long = 0,
	@SerializedName("dislike")
	val dislike: Long = 0,
	@SerializedName("global_id")
	val globalId: Long = 0,
	@SerializedName("global_owner")
	val globalOwner: String? = null
)