package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.CardResponse
import com.uogames.dto.global.GlobalCard


object CardMap : Map<CardResponse, GlobalCard> {

	override fun CardResponse.toDTO() = GlobalCard(
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

	override fun GlobalCard.toResponse() = CardResponse(
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