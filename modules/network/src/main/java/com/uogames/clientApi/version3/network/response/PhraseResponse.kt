package com.uogames.clientApi.version3.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class PhraseResponse(
	@SerializedName("global_id")
	val globalId: UUID,
	@SerializedName("global_owner")
	var globalOwner: String = "",
	@SerializedName("phrase")
	var phrase: String = "",
	@SerializedName("definition")
	var definition: String? = null,
	@SerializedName("lang")
	var lang: String = "eng",
	@SerializedName("country")
	var country: String = "BELARUS",
	@SerializedName("id_pronounce")
	var idPronounce: UUID? = null,
	@SerializedName("id_image")
	var idImage: UUID? = null,
	@SerializedName("time_change")
	var timeChange: Long = 0,
	@SerializedName("like")
	var like: Long = 0,
	@SerializedName("dislike")
	var dislike: Long = 0
)