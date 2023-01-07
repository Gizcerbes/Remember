package com.uogames.database.map

import com.uogames.database.entity.ModuleEntity
import com.uogames.dto.local.LocalModule

object ModuleMap : Map<ModuleEntity, LocalModule> {

	override fun ModuleEntity.toDTO() = LocalModule(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)


	override fun LocalModule.toEntity() = ModuleEntity(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner
	)

}