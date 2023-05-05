package com.uogames.database.map

import com.uogames.database.entity.CardEntity
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhraseView

object CardMap : Map<CardEntity, LocalCard> {
	override fun CardEntity.toDTO() = LocalCard(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner,
		changed = changed
	)


	override fun LocalCard.toEntity() = CardEntity(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner,
		changed = changed
	)

}

class CardViewMap(
	private val phraseBuilder: suspend (id:Int) -> LocalPhraseView,
	private val imageBuilder: suspend (id: Int) -> LocalImageView?
): ViewMap<CardEntity, LocalCardView> {
	override suspend fun toDTO(entity: CardEntity) = LocalCardView(
		id = entity.id,
		phrase = phraseBuilder(entity.idPhrase),
		translate = phraseBuilder(entity.idTranslate),
		image = entity.idImage?.let { imageBuilder(it) },
		reason = entity.reason,
		timeChange = entity.timeChange,
		like = entity.like,
		dislike = entity.dislike,
		globalId = entity.globalId,
		globalOwner = entity.globalOwner,
		changed = entity.changed
	)
	override suspend fun toEntity(dto: LocalCardView) = CardEntity(
		id = dto.id,
		idPhrase = dto.phrase.id,
		idTranslate = dto.translate.id,
		idImage = dto.image?.id,
		reason = dto.reason,
		timeChange = dto.timeChange,
		like = dto.like,
		dislike = dto.dislike,
		globalId = dto.globalId,
		globalOwner = dto.globalOwner,
		changed = dto.changed
	)
}