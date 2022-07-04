package com.uogames.database.map

import com.uogames.database.entity.PhraseEntity
import com.uogames.dto.Phrase

object PhraseMap : Map<PhraseEntity, Phrase> {

	override fun PhraseEntity.toDTO(): Phrase {
		return Phrase(
			id = id,
			phrase = phrase,
			lang = lang,
			idPronounce = idPronounce,
			idImage = idImage,
			definition = definition,
			timeChange = timeChange,
			like = like,
			dislike = dislike
		)
	}

	override fun Phrase.toEntity(): PhraseEntity {
		return PhraseEntity(
			id = id,
			phrase = phrase,
			lang = lang,
			idPronounce = idPronounce,
			idImage = idImage,
			definition = definition,
			timeChange = timeChange,
			like = like,
			dislike = dislike
		)
	}

}