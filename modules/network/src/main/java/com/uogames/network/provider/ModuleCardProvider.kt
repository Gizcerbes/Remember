package com.uogames.network.provider

import com.uogames.dto.global.ModuleCard
import com.uogames.network.map.ModuleCardMap.toDTO
import com.uogames.network.map.ModuleCardMap.toResponse
import com.uogames.network.service.ModuleCardService

class ModuleCardProvider(private val service: ModuleCardService) {

	suspend fun get(moduleID: Long, number: Long) = service.get(moduleID, number).toDTO()

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun count(globalID: Long) = service.count(globalID)

	suspend fun post(moduleCardResponse: ModuleCard) = service.post(moduleCardResponse.toResponse()).toDTO()


}