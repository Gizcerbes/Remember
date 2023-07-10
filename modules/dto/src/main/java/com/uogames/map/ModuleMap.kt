package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.global.GlobalModule
import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalModule

object ModuleMap {

	fun LocalModule.toGlobal() = GlobalModule(
		globalId = globalId,
		globalOwner = globalOwner ?: "",
		name = name,
		timeChange = timeChange,
		like = 0,
		dislike = 0
	)

	fun LocalModule.update(module: GlobalModule) = LocalModule(
		id = id,
		name = module.name,
		owner = owner,
		timeChange = module.timeChange,
		like = module.like,
		dislike = module.dislike,
		globalId = module.globalId,
		globalOwner = module.globalOwner
	)

	fun GlobalModuleView.toGlobalModule() = GlobalModule(
		globalId = globalId,
		globalOwner = user.globalOwner,
		name = name,
		timeChange = timeChange,
		like = like,
		dislike = dislike
	)

}