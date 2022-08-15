package com.uogames.network.provider

import com.uogames.network.map.PhraseMap.toDTO
import com.uogames.network.response.PhraseResponse
import com.uogames.network.service.PhraseService

class PhraseProvider(private val service: PhraseService) {

	suspend fun count(like: String): Long = service.count(like)

	suspend fun get(like:String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun post(phraseResponse: PhraseResponse) = service.post(phraseResponse).toDTO()

}