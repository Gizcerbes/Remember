package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCard

object ModuleCardMap {

	fun LocalModuleCard.toGlobal(module: LocalModule?, card: LocalCard?) = com.uogames.dto.global.GlobalModuleCard(
		globalId = globalId ?: DefaultUUID.value,
		globalOwner = globalOwner ?: "",
		idModule = module?.globalId ?: DefaultUUID.value,
		idCard = card?.globalId ?: DefaultUUID.value
	)

	fun LocalModuleCard.update(moduleCard: com.uogames.dto.global.GlobalModuleCard) = LocalModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = moduleCard.globalId,
		globalOwner = moduleCard.globalOwner
	)


}