package com.uogames.repository.map

import com.uogames.database.entity.ErrorCardEntity
import com.uogames.dto.local.ErrorCard

object ErrorCardMap {

	fun ErrorCardEntity.toDTO() = ErrorCard(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			correct = correct,
			incorrect = incorrect,
			percentCorrect = percentCorrect
		)

	 fun ErrorCard.toEntity() = ErrorCardEntity(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			correct = correct,
			incorrect = incorrect,
			percentCorrect = percentCorrect
		)



}