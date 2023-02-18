package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase

object CardMap {

	fun LocalCard.toGlobal(phrase: LocalPhrase?, translate: LocalPhrase?, image: LocalImage?) = com.uogames.dto.global.GlobalCard(
		globalId = globalId ?: DefaultUUID.value,
		globalOwner = globalOwner ?: "",
		idPhrase = phrase?.globalId ?: DefaultUUID.value,
		idTranslate = translate?.globalId ?: DefaultUUID.value,
		idImage = image?.globalId ?: DefaultUUID.value,
		reason = reason,
		timeChange = timeChange,
		like = 0,
		dislike = 0
	)

	fun LocalCard.update(card: com.uogames.dto.global.GlobalCard) = LocalCard(
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

	fun LocalCard.update(card: com.uogames.dto.global.GlobalCard, idPhrase: Int, idTranslate: Int, idImage: Int?) = LocalCard(
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