package com.uogames.repository.map

import com.uogames.database.entity.CardEntity
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhraseView
import java.util.UUID

object CardMap {

	fun CardEntity.toDTO() = LocalCard(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)

	fun LocalCard.toEntity() = CardEntity(
		id = id,
		idPhrase = idPhrase,
		idTranslate = idTranslate,
		idImage = idImage,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)

	suspend fun CardEntity.toViewDTO(
		phraseBuilder: suspend (id:Int) -> LocalPhraseView,
		imageBuilder: suspend (id: Int) -> LocalImageView?
	)= LocalCardView(
		id = id,
		phrase = phraseBuilder(idPhrase),
		translate = phraseBuilder(idTranslate),
		image = idImage?.let { imageBuilder(it) },
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)

	suspend fun LocalCardView.toEntity()  = CardEntity(
		id = id,
		idPhrase = phrase.id,
		idTranslate = translate.id,
		idImage = image?.id,
		reason = reason,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)


}