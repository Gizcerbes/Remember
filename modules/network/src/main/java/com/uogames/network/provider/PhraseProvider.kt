package com.uogames.network.provider

import com.uogames.dto.global.Phrase
import com.uogames.network.map.PhraseMap.toDTO
import com.uogames.network.map.PhraseMap.toResponse
import com.uogames.network.service.PhraseService

class PhraseProvider(private val service: PhraseService) {

	suspend fun count(like: String): Long = service.count(like)

	suspend fun get(like:String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun post(phraseResponse: Phrase) = service.post(phraseResponse.toResponse()).toDTO()

}