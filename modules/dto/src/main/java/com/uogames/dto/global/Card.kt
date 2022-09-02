package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*


data class Card(
	val globalId: UUID = DefaultUUID.value,
	val globalOwner: String,
	val idPhrase: UUID = DefaultUUID.value,
	val idTranslate: UUID = DefaultUUID.value,
	val idImage: UUID? = null,
	val reason: String = "",
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0,

	)
