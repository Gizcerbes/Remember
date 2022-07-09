package com.uogames.database.map

import com.uogames.database.entity.ErrorCardEntity
import com.uogames.dto.ErrorCard

object ErrorCardMap : Map<ErrorCardEntity, ErrorCard> {
	override fun ErrorCardEntity.toDTO(): ErrorCard {
		return ErrorCard(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			correct = correct,
			incorrect = incorrect,
			percentCorrect = percentCorrect
		)
	}

	override fun ErrorCard.toEntity(): ErrorCardEntity {
		return ErrorCardEntity(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			correct = correct,
			incorrect = incorrect,
			percentCorrect = percentCorrect
		)
	}
}