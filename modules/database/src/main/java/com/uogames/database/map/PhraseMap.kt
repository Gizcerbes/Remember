package com.uogames.database.map

import com.uogames.database.entity.PhraseEntity
import com.uogames.dto.local.LocalPhrase

object PhraseMap : Map<PhraseEntity, LocalPhrase> {

	override fun PhraseEntity.toDTO() = LocalPhrase(
		id = id,
		phrase = phrase,
		lang = lang,
		country = country,
		idPronounce = idPronounce,
		idImage = idImage,
		definition = definition,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun LocalPhrase.toEntity() = PhraseEntity(
		id = id,
		phrase = phrase,
		lang = lang,
		country = country,
		idPronounce = idPronounce,
		idImage = idImage,
		definition = definition,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)


}