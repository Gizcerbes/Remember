package com.uogames.database.map

import com.uogames.database.entity.PhraseEntity
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciationView
import java.util.UUID

object PhraseMap : Map<PhraseEntity, LocalPhrase> {

    override fun PhraseEntity.toDTO() = LocalPhrase(
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
        globalId = globalId,
        globalOwner = globalOwner,
        changed = changed
    )


    override fun LocalPhrase.toEntity() = PhraseEntity(
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
        globalId = globalId,
        globalOwner = globalOwner,
        changed = changed
    )


}

class PhraseViewMap(
    private val pronounceBuilder: suspend (id: Int) -> LocalPronunciationView?,
    private val imageBuilder: suspend (id: Int) -> LocalImageView?
) : ViewMap<PhraseEntity, LocalPhraseView> {

    override suspend fun toDTO(entity: PhraseEntity) = LocalPhraseView(
        id = entity.id,
        phrase = entity.phrase,
        definition = entity.definition,
        lang = entity.lang,
        country = entity.country,
        pronounce = entity.idPronounce?.let { pronounceBuilder(it) },
        image = entity.idImage?.let { imageBuilder(it) },
        timeChange = entity.timeChange,
        like = entity.like,
        dislike = entity.dislike,
        globalId = entity.globalId,
        globalOwner = entity.globalOwner,
        changed = entity.changed
    )

    override suspend fun toEntity(dto: LocalPhraseView) = PhraseEntity(
        id = dto.id,
        phrase = dto.phrase,
        definition = dto.definition,
        lang = dto.lang,
        country = dto.country,
        idPronounce = dto.pronounce?.id,
        idImage = dto.image?.id,
        timeChange = dto.timeChange,
        like = dto.like,
        dislike = dto.dislike,
        globalId = dto.globalId,
        globalOwner = dto.globalOwner,
        changed = dto.changed
    )

}