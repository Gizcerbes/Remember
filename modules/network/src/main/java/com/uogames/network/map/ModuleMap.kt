package com.uogames.network.map

import com.uogames.dto.global.GlobalModule
import com.uogames.network.response.ModuleResponse

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