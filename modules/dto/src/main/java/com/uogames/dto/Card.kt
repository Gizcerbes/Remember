package com.uogames.dto

data class Card(
	val id: Int = 0,
	val idPhrase: Int = 0,
	val idTranslate: Int = 0,
	val idImage: Int?,
	val reason:String = "",
	val like: Long = 0,
	val dislike: Long = 0
)