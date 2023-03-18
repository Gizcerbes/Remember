package com.uogames.database.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.local.LocalPronunciation

object PronunciationMap : Map<PronunciationEntity, LocalPronunciation> {
	override fun PronunciationEntity.toDTO() = LocalPronunciation(
		id = id,
		audioUri = audioUri,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun LocalPronunciation.toEntity() = PronunciationEntity(
		id = id,
		audioUri = audioUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

}