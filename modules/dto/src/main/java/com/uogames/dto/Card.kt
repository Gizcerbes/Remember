package com.uogames.dto

import java.util.*

data class Card(
	val id: Int,
	val idPhrase: Int,
	val idTranslate: Int,
	val idImage: Int?,
	val reason:String
)