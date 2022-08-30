package com.uogames.map

import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation

object PhraseMap {

	fun Phrase.toGlobal(image: Image?, pronunciation: Pronunciation?) = com.uogames.dto.global.Phrase(
		globalId = globalId ?: 0,
		globalOwner = globalOwner ?: "",
		phrase = phrase,
		definition = definition,
		lang = lang,
		idPronounce = pronunciation?.globalId,
		idImage = image?.globalId,
		timeChange = timeChange,
		like = 0,
		dislike = 0
	)

	fun Phrase.update(phrase: com.uogames.dto.global.Phrase) = Phrase(
		id = id,
		phrase = phrase.phrase,
		definition = phrase.definition,
		lang = phrase.lang,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = phrase.timeChange,
		like = phrase.like,
		dislike = phrase.dislike,
		globalId = phrase.globalId,
		globalOwner = phrase.globalOwner
	)

	fun Phrase.update(phrase: com.uogames.dto.global.Phrase, idPronounce: Int?, idImage: Int?) = Phrase(
		id = id,
		phrase = phrase.phrase,
		definition = phrase.definition,
		lang = phrase.lang,
		idPronounce = idPronounce,
		idImage = idImage,
		timeChange = phrase.timeChange,
		like = phrase.like,
		dislike = phrase.dislike,
		globalId = phrase.globalId,
		globalOwner = phrase.globalOwner
	)
}