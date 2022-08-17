package com.uogames.map

import com.uogames.dto.local.Card
import com.uogames.dto.local.Module
import com.uogames.dto.local.ModuleCard

object ModuleCardMap {

	fun ModuleCard.toGlobal(module: Module?, card: Card?) = com.uogames.dto.global.ModuleCard(
		globalId = globalId ?: 0,
		globalOwner = globalOwner ?: "",
		idModule = module?.globalId ?: 0,
		idCard = card?.globalId ?: 0
	)

	fun ModuleCard.update(moduleCard: com.uogames.dto.global.ModuleCard) = ModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = moduleCard.globalId,
		globalOwner = globalOwner
	)


}