package com.uogames.database.map

import com.uogames.database.entity.NewCardEntity
import com.uogames.dto.NewCard

object NewCardMap : Map<NewCardEntity, NewCard> {
	override fun NewCardEntity.toDTO(): NewCard {
		return NewCard(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			idImgBase64 = idImgBase64
		)
	}

	override fun NewCard.toEntity(): NewCardEntity {
		return NewCardEntity(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			idImgBase64 = idImgBase64
		)
	}
}