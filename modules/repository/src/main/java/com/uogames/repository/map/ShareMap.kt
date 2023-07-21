package com.uogames.repository.map

import com.uogames.database.entity.ShareEntity
import com.uogames.dto.local.LocalShare

object ShareMap {

	fun ShareEntity.toDTO() = LocalShare(
		id = id,
		idImage = idImage,
		idPronounce = idPronounce,
		idPhrase = idPhrase,
		idCard = idCard,
		idModule = idModule,
		idModuleCard = idModuleCard
	)

	fun LocalShare.toEntity() = ShareEntity(
		id = id,
		idImage = idImage,
		idPronounce = idPronounce,
		idPhrase = idPhrase,
		idCard = idCard,
		idModule = idModule,
		idModuleCard = idModuleCard
	)


}