package com.uogames.repository.map

import com.uogames.database.entity.ModuleCardEntity
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalModuleCardView
import com.uogames.dto.local.LocalModuleView
import java.util.UUID

object ModuleCardMap {

	fun ModuleCardEntity.toDTO() = LocalModuleCard(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner
	)


	fun LocalModuleCard.toEntity() = ModuleCardEntity(
		id = id,
		idModule = idModule,
		idCard = idCard,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)

	suspend fun ModuleCardEntity.toViewDTO(
		moduleBuilder: suspend (id: Int) -> LocalModuleView,
		cardBuilder: suspend (id: Int) -> LocalCardView
	) = LocalModuleCardView(
		id = id,
		module = moduleBuilder(idModule),
		card = cardBuilder(idCard),
		globalId = UUID.fromString(globalId) ?: UUID.randomUUID(),
		globalOwner = globalOwner
	)

	fun LocalModuleCardView.toEntity() = ModuleCardEntity(
		id = id,
		idModule = module.id,
		idCard = card.id,
		globalId = globalId.toString(),
		globalOwner = globalOwner
	)


}