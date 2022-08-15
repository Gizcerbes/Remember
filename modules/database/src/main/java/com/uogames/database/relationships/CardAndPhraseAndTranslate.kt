package com.uogames.database.relationships


import androidx.room.Embedded
import androidx.room.Relation
import com.uogames.database.entity.CardEntity
import com.uogames.dto.local.Phrase

data class CardAndPhraseAndTranslate(
	@Embedded
	val card: CardEntity,
	@Relation(
		parentColumn = "",
		entityColumn = ""
	)
	val phrase: Phrase,
	@Relation(
		parentColumn = "",
		entityColumn = ""
	)
	val translate: Phrase
	)