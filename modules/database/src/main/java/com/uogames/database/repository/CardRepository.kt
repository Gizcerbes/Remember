package com.uogames.database.repository

import com.uogames.database.dao.CardDAO
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.dto.local.Card
import kotlinx.coroutines.flow.map

class CardRepository(private val dao: CardDAO) {

	suspend fun insert(card: Card) = dao.insert(card.toEntity())

	suspend fun delete(card: Card) = dao.delete(card.toEntity()) > 0

	suspend fun update(card: Card) = dao.update(card.toEntity()) > 0

	fun getCountFlow(like:String) = dao.getCountFlow(like)

	fun getCountFlow() = dao.getCountFlow()

	fun getCardFlow(like: String, number: Int) = dao.getCardFlow(like, number).map { it?.toDTO() }

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getRandom() = dao.getRandom()?.toDTO()

	suspend fun getRandomWithOut(id: Int) = dao.getRandomWithOut(id)?.toDTO()


}