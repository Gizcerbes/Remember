package com.uogames.network.map

import com.uogames.dto.global.Phrase
import com.uogames.network.response.PhraseResponse

object PhraseMap : Map<PhraseResponse, Phrase> {
	override fun PhraseResponse.toDTO() = Phrase(
		globalId = globalId,
		globalOwner = globalOwner,
		phrase = phrase,
		definition = definition,
		lang = lang,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	override fun Phrase.toResponse() = PhraseResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		phrase = phrase,
		definition = definition,
		lang = lang,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)
}