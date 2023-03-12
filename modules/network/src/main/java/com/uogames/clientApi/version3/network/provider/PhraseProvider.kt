package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.PhraseMap.toDTO
import com.uogames.clientApi.version3.network.map.PhraseMap.toResponse
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

    suspend fun post(phrase: GlobalPhrase) = s.post(phrase.toResponse()).toDTO()

}