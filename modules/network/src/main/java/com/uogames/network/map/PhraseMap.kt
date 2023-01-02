package com.uogames.network.map

import com.uogames.dto.global.GlobalPhrase
import com.uogames.network.response.PhraseResponse

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