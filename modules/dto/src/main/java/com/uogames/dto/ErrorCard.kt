package com.uogames.dto

data class ErrorCard(
	val id: Long = 0,
	val idPhrase: Int,
	val idTranslate: Int,
	val correct: Long = 0,
	val incorrect: Long = 0,
	val percentCorrect: Byte = 100
)