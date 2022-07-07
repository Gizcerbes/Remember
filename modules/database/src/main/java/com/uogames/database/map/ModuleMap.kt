package com.uogames.database.map

import com.uogames.database.entity.ModuleEntity
import com.uogames.dto.Module

object ModuleMap : Map<ModuleEntity, Module> {
	override fun ModuleEntity.toDTO(): Module {
		return Module(
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

	override fun Module.toEntity(): ModuleEntity {
		return ModuleEntity(
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
}