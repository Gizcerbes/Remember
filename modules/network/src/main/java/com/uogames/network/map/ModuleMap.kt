package com.uogames.network.map

import com.uogames.dto.global.Module
import com.uogames.network.response.ModuleResponse

object ModuleMap : Map<ModuleResponse, Module> {

	override fun ModuleResponse.toDTO() = Module(
		globalId = globalId,
		globalOwner = globalOwner,
		name = name,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

	override fun Module.toResponse() = ModuleResponse(
		globalId = globalId,
		globalOwner = globalOwner,
		name = name,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

}