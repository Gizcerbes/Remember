package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*


data class Phrase(
	val globalId: UUID = DefaultUUID.value,
	val globalOwner: String,
	val phrase: String = "",
	val definition: String? = null,
	val lang: String = "eng",
	val country:String = "BELARUS",
	val idPronounce: UUID? = null,
	val idImage: UUID? = null,
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0
)