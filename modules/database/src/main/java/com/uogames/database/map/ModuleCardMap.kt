package com.uogames.database.map

import com.uogames.database.entity.ModuleCardEntity
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalModuleCardView
import com.uogames.dto.local.LocalModuleView
import java.util.UUID

object ModuleCardMap : Map<ModuleCardEntity, LocalModuleCard> {

    override fun ModuleCardEntity.toDTO() = LocalModuleCard(
        id = id,
        idModule = idModule,
        idCard = idCard,
        globalId = globalId ?: UUID.randomUUID(),
        globalOwner = globalOwner
    )


    override fun LocalModuleCard.toEntity() = ModuleCardEntity(
        id = id,
        idModule = idModule,
        idCard = idCard,
        globalId = globalId,
        globalOwner = globalOwner
    )

}

class ModuleCardViewMap(
    private val moduleBuilder: suspend (id: Int) -> LocalModuleView,
    private val cardBuilder: suspend (id: Int) -> LocalCardView
) : ViewMap<ModuleCardEntity, LocalModuleCardView> {

    override suspend fun toDTO(entity: ModuleCardEntity) = LocalModuleCardView(
        id = entity.id,
        module = moduleBuilder(entity.idModule),
        card = cardBuilder(entity.idCard),
        globalId = entity.globalId ?: UUID.randomUUID(),
        globalOwner = entity.globalOwner
    )

    override suspend fun toEntity(dto: LocalModuleCardView) = ModuleCardEntity(
        id = dto.id,
        idModule = dto.module.id,
        idCard = dto.card.id,
        globalId = dto.globalId,
        globalOwner = dto.globalOwner
    )

}