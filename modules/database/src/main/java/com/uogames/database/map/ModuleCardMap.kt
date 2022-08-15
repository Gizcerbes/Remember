package com.uogames.database.map

import com.uogames.database.entity.ModuleCardEntity
import com.uogames.dto.local.ModuleCard

object ModuleCardMap : Map<ModuleCardEntity, ModuleCard> {
	override fun ModuleCardEntity.toDTO(): ModuleCard {
		return ModuleCard(
			id = id,
			idModule = idModule,
			idCard = idCard
		)
	}

	override fun ModuleCard.toEntity(): ModuleCardEntity {
		return ModuleCardEntity(
			id = id,
			idModule = idModule,
			idCard = idCard
		)
	}
}