package com.uogames.database.map

import com.uogames.database.entity.PronunciationEntity
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalPronunciation
import com.uogames.dto.local.LocalPronunciationView
import java.util.UUID

object PronunciationMap : Map<PronunciationEntity, LocalPronunciation> {
	override fun PronunciationEntity.toDTO() = LocalPronunciation(
		id = id,
		audioUri = audioUri,
		globalId = globalId ?: UUID.randomUUID(),
		globalOwner = globalOwner
	)


	override fun LocalPronunciation.toEntity() = PronunciationEntity(
		id = id,
		audioUri = audioUri,
		globalId = globalId,
		globalOwner = globalOwner
	)

}

class PronunciationViewMap(): ViewMap<PronunciationEntity, LocalPronunciationView> {

	override suspend fun toDTO(entity: PronunciationEntity) = LocalPronunciationView(
		id = entity.id,
		audioUri = entity.audioUri,
		globalOwner = entity.globalOwner,
		globalId = entity.globalId ?: UUID.randomUUID()
	)

	override suspend fun toEntity(dto: LocalPronunciationView) = PronunciationEntity(
		id = dto.id,
		audioUri = dto.audioUri,
		globalId = dto.globalId,
		globalOwner = dto.globalOwner
	)


}

