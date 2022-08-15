package com.uogames.dto.global


data class Card(
	val globalId: Long = 0,
	val globalOwner: String,
	val idPhrase: Long = 0,
	val idTranslate: Long = 0,
	val idImage: Long? = null,
	val reason: String = "",
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0,

)
