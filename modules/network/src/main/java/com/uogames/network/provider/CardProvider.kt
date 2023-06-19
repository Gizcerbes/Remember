package com.uogames.network.provider

import com.uogames.dto.global.GlobalCard
import com.uogames.network.map.CardMap.toDTO
import com.uogames.network.map.CardMap.toResponse
import com.uogames.network.service.CardService
import java.util.*

class CardProvider(private val service: CardService) {

	suspend fun get(like: String, number: Long) = service.get(like, number).toDTO()

	suspend fun get(globalId: UUID) = service.get(globalId).toDTO()

	suspend fun count(like: String) = service.count(like)

	suspend fun post(cardResponse: GlobalCard) = service.post(cardResponse.toResponse()).toDTO()


}