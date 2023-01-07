package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.local.LocalModule

object ModuleMap {

	fun LocalModule.toGlobal() = com.uogames.dto.global.Module(
		globalId = globalId ?: DefaultUUID.value,
		globalOwner = globalOwner ?: "",
		name = name,
		timeChange = timeChange,
		like = 0,
		dislike = 0
	)

	fun LocalModule.update(module: com.uogames.dto.global.Module) = LocalModule(
		id = id,
		name = module.name,
		owner = owner,
		timeChange = module.timeChange,
		like = module.like,
		dislike = module.dislike,
		globalId = module.globalId,
		globalOwner = module.globalOwner
	)

}