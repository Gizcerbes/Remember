package com.uogames.dto.local

import java.util.*

data class Phrase(
	val id: Int = 0,
	val phrase: String = "",
	val definition: String? = null,
	val lang: String= "eng",
	val country: String = "UNITED_KINGDOM",
	val idPronounce: Int? = null,
	val idImage: Int? = null,
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)