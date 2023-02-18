package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.ModuleCardResponse
import com.uogames.dto.global.GlobalModuleCard

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