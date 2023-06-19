package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.ModuleResponse
import com.uogames.dto.global.GlobalModule

object ModuleMap : Map<ModuleResponse, GlobalModule> {

	override fun ModuleResponse.toDTO() = GlobalModule(
		globalId = globalId,
		globalOwner = globalOwner,
		name = name,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	override fun GlobalModule.toResponse() = ModuleResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		name = name,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

}