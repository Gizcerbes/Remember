package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.ModuleMap.toDTO
import com.uogames.clientApi.version3.network.map.ModuleMap.toResponse
import com.uogames.clientApi.version3.network.service.ModuleService
import com.uogames.dto.global.GlobalModule
import java.util.*

class ModuleProvider(private val s: ModuleService) {

    suspend fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ) = s.count(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond
    )

    suspend fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = s.get(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).toDTO()

    suspend fun get(globalId: UUID) = s.get(globalId).toDTO()

    suspend fun post(module: GlobalModule) = s.post(module.toResponse()).toDTO()


}