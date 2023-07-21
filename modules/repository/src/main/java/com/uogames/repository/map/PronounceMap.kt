package com.uogames.repository.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPronunciation
import com.uogames.dto.local.LocalPronunciationView
import java.util.UUID

object PronounceMap {

	fun PronunciationEntity.toDTO() = LocalPronunciation(
		id = id,
		audioUri = audioUri,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner
	)


	fun LocalPronunciation.toEntity() = PronunciationEntity(
		id = id,
		audioUri = audioUri,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)


	fun PronunciationEntity.toViewDTO() = LocalPronunciationView(
		id = id,
		audioUri = audioUri,
		globalId = UUID.fromString(globalId),
		globalOwner = globalOwner
	)

	fun LocalPronunciationView.toEntity() = PronunciationEntity(
		id = id,
		audioUri = audioUri,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)

}