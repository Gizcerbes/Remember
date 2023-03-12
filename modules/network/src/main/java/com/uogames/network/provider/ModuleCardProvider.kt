package com.uogames.network.provider

import com.uogames.dto.global.GlobalModuleCard
import com.uogames.network.map.ModuleCardMap.toDTO
import com.uogames.network.map.ModuleCardMap.toResponse
import com.uogames.network.service.ModuleCardService
import java.util.*

class ModuleCardProvider(private val service: ModuleCardService) {

	suspend fun get(moduleID: UUID, number: Long) = service.get(moduleID, number).toDTO()

	suspend fun get(globalId: UUID) = service.get(globalId).toDTO()

	suspend fun count(globalID: UUID) = service.count(globalID)

	suspend fun post(moduleCardResponse: GlobalModuleCard) = service.post(moduleCardResponse.toResponse()).toDTO()


}