package com.uogames.dto

data class Phrase(
	val id: Int,
	val phrase: String,
	val definition: String?,
	val lang: String?,
	val idPronounce: Int?,
	val idImage: Int?,
	val timeChange: Long
)