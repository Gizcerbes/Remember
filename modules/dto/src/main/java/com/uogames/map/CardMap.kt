package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase

object CardMap {

	fun LocalCard.toGlobal(phrase: LocalPhrase?, translate: LocalPhrase?, image: LocalImage?) = GlobalCard(
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

	fun LocalCard.update(card: GlobalCard) = LocalCard(
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

	fun LocalCard.update(card: GlobalCard, idPhrase: Int, idTranslate: Int, idImage: Int?) = LocalCard(
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

	fun LocalCard.update(view: GlobalCardView, idPhrase: Int, idTranslate: Int, idImage: Int?) = LocalCard(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		reason = view.reason,
		idImage = idImage,
		timeChange = view.timeChange,
		like = like,
		dislike = dislike,
		globalId = view.globalId,
		globalOwner = view.user.globalOwner
	)

	fun GlobalCardView.toGlobalCard() = GlobalCard(
		globalId = globalId,
		globalOwner = user.globalOwner,
		idPhrase = phrase.globalId,
		idTranslate = translate.globalId,
		idImage = image?.globalId,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	fun LocalCardView.toLocalCard() = LocalCard(
		id = id,
		idPhrase = phrase.id,
		idTranslate = translate.id,
		idImage = image?.id,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)

}