package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.CardMap.toDTO
import com.uogames.clientApi.version3.network.map.CardMap.toResponse
import com.uogames.clientApi.version3.network.map.CardViewMap.toDTO
import com.uogames.clientApi.version3.network.service.CardService
import com.uogames.dto.global.GlobalCard
import java.util.*

class CardProvider(private val s: CardService) {

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

    suspend fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = s.getView(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).toDTO()

    suspend fun getListView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long,
        limit: Int
    ) = s.getListView(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number,
        limit = limit
    ).map { it.toDTO() }

    suspend fun getView(globalId: UUID) = s.getView(globalId).toDTO()

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

    suspend fun post(card: GlobalCard) = s.post(card.toResponse()).toDTO()

}