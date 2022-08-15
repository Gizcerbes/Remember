package com.uogames.network.provider

import com.uogames.dto.global.Module
import com.uogames.network.map.ModuleMap.toDTO
import com.uogames.network.map.ModuleMap.toResponse
import com.uogames.network.response.ModuleResponse
import com.uogames.network.service.ModuleService

class ModuleProvider(private val service: ModuleService) {

    suspend fun get(like: String, number: Long) = service.get(like, number).toDTO()

    suspend fun get(globalId: Long) = service.get(globalId).toDTO()

    suspend fun count(like: String) = service.count(like)

    suspend fun post(module: Module) = service.post(module.toResponse()).toDTO()


}