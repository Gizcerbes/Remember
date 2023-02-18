package com.uogames.network.map

import com.uogames.dto.global.GlobalModuleCard
import com.uogames.network.response.ModuleCardResponse

object ModuleCardMap : Map<ModuleCardResponse, GlobalModuleCard> {

	override fun ModuleCardResponse.toDTO() = GlobalModuleCard(
		globalId = globalId,
		globalOwner = globalOwner,
		idModule = idModule,
		idCard = idCard
	)


	override fun GlobalModuleCard.toResponse() = ModuleCardResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		idModule = idModule,
		idCard = idCard
	)

}