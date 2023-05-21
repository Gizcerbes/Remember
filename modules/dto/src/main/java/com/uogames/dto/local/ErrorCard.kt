package com.uogames.dto.local

data class ErrorCard(
	val id: Long = 0,
	val idPhrase: Int,
	val idTranslate: Int,
	var correct: Long = 0,
	var incorrect: Long = 0,
	var percentCorrect: Byte = 100
)