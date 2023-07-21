package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.global.GlobalModuleCard
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalModuleCardView
import java.util.UUID

object ModuleCardMap {

    fun LocalModuleCard.toGlobal(module: LocalModule?, card: LocalCard?) = GlobalModuleCard(
        globalId = globalId,
        globalOwner = globalOwner ?: "",
        idModule = module?.globalId ?: DefaultUUID.value,
        idCard = card?.globalId ?: DefaultUUID.value
    )

    fun LocalModuleCard.toGlobal(moduleID: UUID, cardID: UUID) = GlobalModuleCard(
        globalId = globalId,
        globalOwner = globalOwner ?: "",
        idModule = moduleID,
        idCard = cardID
    )

    fun LocalModuleCard.update(moduleCard: GlobalModuleCard) = LocalModuleCard(
        id = id,
        idModule = idModule,
        idCard = idCard,
        globalId = moduleCard.globalId,
        globalOwner = moduleCard.globalOwner
    )

    fun LocalModuleCardView.toLocalModuleCard() = LocalModuleCard(
		id = id,
        idModule = module.id,
        idCard = card.id,
        globalId = globalId,
        globalOwner = globalOwner
    )


}