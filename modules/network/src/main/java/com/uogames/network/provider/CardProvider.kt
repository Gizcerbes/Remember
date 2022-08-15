package com.uogames.network.provider

import com.uogames.dto.global.Card
import com.uogames.network.map.CardMap.toDTO
import com.uogames.network.map.CardMap.toResponse
import com.uogames.network.service.CardService

class CardProvider(private val service: CardService) {

	suspend fun get(like: String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun count(like: String) = service.count(like)

	suspend fun post(cardResponse: Card) = service.post(cardResponse.toResponse()).toDTO()


}