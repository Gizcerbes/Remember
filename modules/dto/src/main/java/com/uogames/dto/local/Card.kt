package com.uogames.dto.local

import java.util.*

data class Card(
	val id: Int = 0,
	val idPhrase: Int = 0,
	val idTranslate: Int = 0,
	val idImage: Int? = null,
	val reason: String = "",
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: Long? = null,
	val globalOwner: String? = null
)