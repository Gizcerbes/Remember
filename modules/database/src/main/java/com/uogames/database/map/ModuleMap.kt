package com.uogames.database.map

import com.uogames.database.entity.ModuleEntity
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import java.util.UUID

object ModuleMap : Map<ModuleEntity, LocalModule> {

	override fun ModuleEntity.toDTO() = LocalModule(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId ?: UUID.randomUUID(),
		globalOwner = globalOwner,
		changed = changed
	)


	override fun LocalModule.toEntity() = ModuleEntity(
		id = id,
		name = name,
		owner = owner,
		timeChange = timeChange,
		like = like,
		dislike = dislike,
		globalId = globalId,
		globalOwner = globalOwner,
		changed = changed
	)

}

class ModuleViewMap() : ViewMap<ModuleEntity, LocalModuleView> {
	override suspend fun toDTO(entity: ModuleEntity)= LocalModuleView(
		id = entity.id,
		name = entity.name,
		owner = entity.owner,
		timeChange = entity.timeChange,
		like = entity.like,
		dislike = entity.dislike,
		globalId = entity.globalId ?: UUID.randomUUID(),
		globalOwner = entity.globalOwner,
		changed = entity.changed
	)

	override suspend fun toEntity(dto: LocalModuleView) = ModuleEntity(
		id = dto.id,
		name = dto.name,
		owner = dto.owner,
		timeChange = dto.timeChange,
		like = dto.like,
		dislike = dto.dislike,
		globalId = dto.globalId,
		globalOwner = dto.globalOwner,
		changed = dto.changed
	)
}