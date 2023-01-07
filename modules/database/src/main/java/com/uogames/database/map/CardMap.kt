package com.uogames.database.map

import com.uogames.database.entity.CardEntity
import com.uogames.dto.local.LocalCard

object CardMap : Map<CardEntity, LocalCard> {
	override fun CardEntity.toDTO() = LocalCard(
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


	override fun LocalCard.toEntity() = CardEntity(
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