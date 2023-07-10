package com.uogames.repository.map

import com.uogames.database.entity.PhraseEntity
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

}