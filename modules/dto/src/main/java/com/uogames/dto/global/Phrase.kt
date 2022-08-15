package com.uogames.dto.global


data class Phrase(
	val globalId: Long = 0,
	val globalOwner: String,
	val phrase: String = "",
	val definition: String? = null,
	val lang: String = "eng-gb",
	val idPronounce: Long? = null,
	val idImage: Long? = null,
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0
)