package com.uogames.network.map

import com.uogames.dto.global.Card
import com.uogames.network.response.CardResponse

object CardMap : Map<CardResponse, Card> {

	override fun CardResponse.toDTO() = Card(
		globalId = globalId,
		globalOwner = globalOwner,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	override fun Card.toResponse() = CardResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

}