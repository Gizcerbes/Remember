package com.uogames.database.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.local.Pronunciation

object PronunciationMap : Map<PronunciationEntity, Pronunciation> {
	override fun PronunciationEntity.toDTO() = Pronunciation(
		id = id,
		audioUri = audioUri,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun Pronunciation.toEntity() = PronunciationEntity(
		id = id,
		audioUri = audioUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

}