package com.uogames.database.map

import com.uogames.database.entity.CardEntity
import com.uogames.dto.Card

object CardMap : Map<CardEntity, Card> {
	override fun CardEntity.toDTO(): Card {
		return Card(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			idImgBase64 = idImgBase64
		)
	}

	override fun Card.toEntity(): CardEntity {
		return CardEntity(
			id = id,
			idPhrase = idPhrase,
			idTranslate = idTranslate,
			idImgBase64 = idImgBase64
		)
	}
}