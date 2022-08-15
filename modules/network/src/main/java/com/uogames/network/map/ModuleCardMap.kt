package com.uogames.network.map

import com.uogames.dto.global.ModuleCard
import com.uogames.network.response.ModuleCardResponse

object ModuleCardMap : Map<ModuleCardResponse, ModuleCard> {

	override fun ModuleCardResponse.toDTO() = ModuleCard(
		globalId = globalId,
		globalOwner = globalOwner,
		idModule = idModule,
		idCard = idCard
	)


	override fun ModuleCard.toResponse() = ModuleCardResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		idModule = idModule,
		idCard = idCard
	)

}