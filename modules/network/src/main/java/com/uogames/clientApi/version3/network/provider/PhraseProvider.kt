package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.PhraseMap.toDTO
import com.uogames.clientApi.version3.network.map.PhraseMap.toResponse
import com.uogames.clientApi.version3.network.map.PhraseViewMap.toDTO
import com.uogames.clientApi.version3.network.service.PhraseService
import com.uogames.dto.global.GlobalPhrase
import java.util.*

class PhraseProvider(private val s: PhraseService) {

    suspend fun count(
        text: String? = null,
        lang: String? = null,
        country: String? = null
    ) = s.count(
        text = text,
        lang = lang,
        country = country
    )

    suspend fun get(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long
    ) = s.get(
        text = text,
        lang = lang,
        country = country,
        number = number
    ).toDTO()

    suspend fun get(globalId: UUID) = s.get(globalId).toDTO()

    suspend fun getView(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long
    ) = s.getView(
        text = text,
        lang = lang,
        country = country,
        number = number
    ).toDTO()

    suspend fun getListView(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long,
        limit: Int = 1
    ) = s.getListView(
        text = text,
        lang = lang,
        country = country,
        number = number,
        limit = limit
    ).map { it.toDTO() }

    suspend fun getView(globalId: UUID) = s.getView(globalId).toDTO()

    suspend fun post(phrase: GlobalPhrase) = s.post(phrase.toResponse()).toDTO()

}