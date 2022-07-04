package com.uogames.dto

data class Phrase(
	val id: Int = 0,
	val phrase: String = "",
	val definition: String? = null,
	val lang: String? = null,
	val idPronounce: Int? = null,
	val idImage: Int? = null,
	val timeChange: Long = 0,
	val like: Long =0,
	val dislike: Long = 0
)