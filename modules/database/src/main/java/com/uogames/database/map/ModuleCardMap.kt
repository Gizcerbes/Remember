package com.uogames.database.map

import com.uogames.database.entity.ModuleCardEntity
import com.uogames.dto.local.ModuleCard
import java.util.*

object ModuleCardMap : Map<ModuleCardEntity, ModuleCard> {

	override fun ModuleCardEntity.toDTO() = ModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun ModuleCard.toEntity() = ModuleCardEntity(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = globalId,
		globalOwner = globalOwner
	)

}