package com.uogames.network.provider

import com.uogames.network.map.CardMap.toDTO
import com.uogames.network.response.CardResponse
import com.uogames.network.service.CardService

class CardProvider(private val service: CardService) {

	suspend fun get(like: String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun count(like: String) = service.count(like)

	suspend fun post(cardResponse: CardResponse) = service.post(cardResponse).toDTO()


}