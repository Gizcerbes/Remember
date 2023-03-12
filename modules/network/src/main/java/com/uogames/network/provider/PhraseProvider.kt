package com.uogames.network.provider

import com.uogames.dto.global.GlobalPhrase
import com.uogames.network.map.PhraseMap.toDTO
import com.uogames.network.map.PhraseMap.toResponse
import com.uogames.network.service.PhraseService
import java.util.*

class PhraseProvider(private val service: PhraseService) {

	suspend fun count(like: String): Long = service.count(like)

	suspend fun get(like:String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: UUID) = service.get(globalId).toDTO()

	suspend fun post(phraseResponse: GlobalPhrase) = service.post(phraseResponse.toResponse()).toDTO()

}