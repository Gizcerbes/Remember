package com.uogames.dto.local

import java.util.*

data class Phrase(
	val id: Int = 0,
	val phrase: String = "",
	val definition: String? = null,
	val lang: String= "eng-gb",
	val idPronounce: Int? = null,
	val idImage: Int? = null,
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: Long? = null,
	val globalOwner: String? = null
)