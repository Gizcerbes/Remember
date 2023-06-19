package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.PhraseResponse
import com.uogames.dto.global.GlobalPhrase

object PhraseMap : Map<PhraseResponse, GlobalPhrase> {
	override fun PhraseResponse.toDTO() = GlobalPhrase(
		globalId = globalId,
		globalOwner = globalOwner,
		phrase = phrase,
		definition = definition,
		lang = lang,
		country = country,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	override fun GlobalPhrase.toResponse() = PhraseResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		phrase = phrase,
		definition = definition,
		lang = lang,
		country = country,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)
}