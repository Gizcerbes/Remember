package com.uogames.database.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.Pronunciation

object PronunciationMap : Map<PronunciationEntity, Pronunciation> {
	override fun PronunciationEntity.toDTO(): Pronunciation {
		return Pronunciation(
			id = id,
			audioUri = audioUri
		)
	}

	override fun Pronunciation.toEntity(): PronunciationEntity {
		return PronunciationEntity(
			id = id,
			audioUri = audioUri
		)
	}
}