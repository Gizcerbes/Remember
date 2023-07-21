package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.ModuleCardMap.toDTO
import com.uogames.clientApi.version3.network.map.ModuleCardMap.toResponse
import com.uogames.clientApi.version3.network.map.ModuleCardViewMap.toDTO
import com.uogames.clientApi.version3.network.response.ModuleCardResponse
import com.uogames.clientApi.version3.network.service.ModuleCardService
import com.uogames.dto.global.GlobalModuleCard
import java.util.*

class ModuleCardProvider(private val s: ModuleCardService) {

    suspend fun count(moduleID: UUID) = s.count(moduleID)

    suspend fun get(
        moduleID: UUID? = null,
        number: Long
    ) = s.get(
        moduleID = moduleID,
        number = number
    ).toDTO()

    suspend fun get(globalId: UUID) = s.get(globalId).toDTO()

    suspend fun getView(
        moduleID: UUID? = null,
        number: Long
    ) = s.getView(
        moduleID = moduleID,
        number = number
    ).toDTO()

    suspend fun getListView(
        moduleID: UUID? = null,
        number: Long,
        limit: Int = 1
    ) = s.getListView(
        moduleID = moduleID,
        number = number,
        limit = limit
    ).map { it.toDTO() }

    suspend fun getView(globalId: UUID) = s.getView(globalId).toDTO()


    suspend fun post(moduleCard: GlobalModuleCard)= s.post(moduleCard.toResponse()).toDTO()


}