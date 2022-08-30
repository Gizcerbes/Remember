package com.uogames.map

import com.uogames.dto.local.Card
import com.uogames.dto.local.Image
import com.uogames.dto.local.Phrase

object CardMap {

	fun Card.toGlobal(phrase: Phrase?, translate: Phrase?, image: Image?) = com.uogames.dto.global.Card(
		globalId = globalId ?: 0,
		globalOwner = globalOwner ?: "",
		idPhrase = phrase?.globalId ?: 0,
		idTranslate = translate?.globalId ?: 0,
		idImage = image?.globalId ?: 0,
		reason = reason,
		timeChange = timeChange,
		like = 0,
		dislike = 0
	)

	fun Card.update(card: com.uogames.dto.global.Card) = Card(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = card.reason,
		timeChange = card.timeChange,
		like = card.like,
		dislike = card.dislike,
		globalId = card.globalId,
		globalOwner = card.globalOwner
	)

	fun Card.update(card: com.uogames.dto.global.Card, idPhrase: Int, idTranslate: Int, idImage: Int?) = Card(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = card.reason,
		timeChange = card.timeChange,
		like = card.like,
		dislike = card.dislike,
		globalId = card.globalId,
		globalOwner = card.globalOwner
	)

}