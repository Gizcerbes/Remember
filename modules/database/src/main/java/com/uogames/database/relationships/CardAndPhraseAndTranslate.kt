package com.uogames.database.relationships


import androidx.room.Embedded
import androidx.room.Relation
import com.uogames.database.entity.NewCardEntity
import com.uogames.dto.Phrase

data class CardAndPhraseAndTranslate(
	@Embedded
	val card: NewCardEntity,
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