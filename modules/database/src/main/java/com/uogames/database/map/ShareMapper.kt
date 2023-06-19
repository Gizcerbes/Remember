package com.uogames.database.map

import com.uogames.database.entity.ShareEntity
import com.uogames.dto.local.LocalShare

object ShareMapper : Map<ShareEntity, LocalShare> {

    override fun ShareEntity.toDTO() = LocalShare(
        id = id,
        idImage = idImage,
        idPronounce = idPronounce,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule,
        idModuleCard = idModuleCard
    )

    override fun LocalShare.toEntity() = ShareEntity(
        id = id,
        idImage = idImage,
        idPronounce = idPronounce,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule,
        idModuleCard = idModuleCard
    )
}