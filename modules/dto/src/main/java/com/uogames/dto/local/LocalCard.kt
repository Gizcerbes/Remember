package com.uogames.dto.local

import java.util.*

data class LocalCard(
	val id: Int = 0,
	val idPhrase: Int = 0,
	val idTranslate: Int = 0,
	val idImage: Int? = null,
	val reason: String = "",
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: UUID? = null,
	val globalOwner: String? = null,
    val changed: Boolean = false
)

data class LocalCardView(
    val id: Int = 0,
    val phrase: LocalPhraseView,
    val translate: LocalPhraseView,
    val image: LocalImageView? = null,
    val reason: String = "",
    val timeChange: Long = Date().time,
    val like: Long = 0,
    val dislike: Long = 0,
    val globalId: UUID? = null,
    val globalOwner: String? = null,
    val changed: Boolean
)