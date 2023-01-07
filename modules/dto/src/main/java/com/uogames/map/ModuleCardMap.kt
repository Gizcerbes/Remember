package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.ModuleCard

object ModuleCardMap {

	fun ModuleCard.toGlobal(module: LocalModule?, card: LocalCard?) = com.uogames.dto.global.ModuleCard(
		globalId = globalId ?: DefaultUUID.value,
		globalOwner = globalOwner ?: "",
		idModule = module?.globalId ?: DefaultUUID.value,
		idCard = card?.globalId ?: DefaultUUID.value
	)

	fun ModuleCard.update(moduleCard: com.uogames.dto.global.ModuleCard) = ModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = moduleCard.globalId,
		globalOwner = moduleCard.globalOwner
	)


}