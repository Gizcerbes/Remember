package com.uogames.database.map

import com.uogames.database.entity.CardEntity
import com.uogames.dto.local.Card

object CardMap : Map<CardEntity, Card> {
	override fun CardEntity.toDTO() = Card(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun Card.toEntity() = CardEntity(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)

}