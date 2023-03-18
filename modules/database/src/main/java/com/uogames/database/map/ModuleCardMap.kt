package com.uogames.database.map

import com.uogames.database.entity.ModuleCardEntity
import com.uogames.dto.local.LocalModuleCard

object ModuleCardMap : Map<ModuleCardEntity, LocalModuleCard> {

	override fun ModuleCardEntity.toDTO() = LocalModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun LocalModuleCard.toEntity() = ModuleCardEntity(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = globalId,
		globalOwner = globalOwner
	)

}