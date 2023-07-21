package com.uogames.repository.map

import com.uogames.database.entity.ModuleEntity
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import java.util.UUID

object ModuleMap {

	fun ModuleEntity.toDTO() = LocalModule(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)


	fun LocalModule.toEntity() = ModuleEntity(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)

	fun ModuleEntity.toViewDTO() = LocalModuleView(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)

	fun LocalModuleView.toEntity() = ModuleEntity(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId.toString(),
		globalOwner = globalOwner,
		changed = changed
	)


}