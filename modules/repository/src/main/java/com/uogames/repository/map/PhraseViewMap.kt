package com.uogames.repository.map

import com.uogames.database.entity.PhraseEntity
import com.uogames.dto.global.GlobalImageView
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciationView
import java.util.UUID

object PhraseViewMap {

	suspend fun PhraseEntity.toViewDTO(
		pronounceBuilder: suspend (id: Int) -> LocalPronunciationView?,
		imageBuilder: suspend (id: Int) -> LocalImageView?
	) = LocalPhraseView(
		id = id,
		phrase = phrase,
		definition = definition,
		lang = lang,
		country = country,
		pronounce = idPronounce?.let { pronounceBuilder(it) },
		image = idImage?.let { imageBuilder(it) },
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)

	suspend fun LocalPhraseView.toEntity() = PhraseEntity(
		id = id,
		phrase = phrase,
		definition = definition,
		lang = lang,
		country = country,
		idPronounce = pronounce?.id,
		idImage = image?.id,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)

	fun PhraseEntity.toDTO() = LocalPhrase(
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
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)

	fun LocalPhrase.toEntity() = PhraseEntity(
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
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)

	suspend fun PhraseEntity.update(
		v: GlobalPhraseView,
		pronounceUpdate: suspend (pv: GlobalPronunciationView) -> Int?,
		imageUpdate: suspend (iv: GlobalImageView) -> Int?
	) = PhraseEntity(
		id = id,
		phrase = v.phrase,
		definition = v.definition,
		lang = v.lang,
		country = v.country,
		idPronounce = v.pronounce?.let { pronounceUpdate(it) },
		idImage = v.image?.let { imageUpdate(it) },
		timeChange = v.timeChange,
		like = v.like,
		dislike = v.dislike,
		globalId = v.globalId.toString(),
		globalOwner = v.user.globalOwner,
		changed = false
	)

	suspend fun GlobalPhraseView.toEntity(
		pronounceUpdate: suspend (pv: GlobalPronunciationView) -> Int?,
		imageUpdate: suspend (iv: GlobalImageView) -> Int?
	) = PhraseEntity(
		id = 0,
		phrase = phrase,
		definition = definition,
		lang = lang,
		country =country,
		idPronounce = pronounce?.let { pronounceUpdate(it) },
		idImage = image?.let { imageUpdate(it) },
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = user.globalOwner,
		changed = false
	)

}