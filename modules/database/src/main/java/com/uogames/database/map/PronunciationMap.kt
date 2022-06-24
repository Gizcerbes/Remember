package com.uogames.database.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.Pronunciation

object PronunciationMap : Map<PronunciationEntity, Pronunciation> {
	override fun PronunciationEntity.toDTO(): Pronunciation {
		return Pronunciation(
			id = id,
			dataBase64 = audioUri
		)
	}

	override fun Pronunciation.toEntity(): PronunciationEntity {
		return PronunciationEntity(
			id = id,
			audioUri = dataBase64
		)
	}
}