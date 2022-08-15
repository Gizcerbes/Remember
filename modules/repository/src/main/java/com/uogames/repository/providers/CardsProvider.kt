package com.uogames.repository.providers

import com.uogames.database.DatabaseRepository
import com.uogames.dto.local.Card
import com.uogames.dto.local.ModuleCard

class CardsProvider(
	private val database: DatabaseRepository
) {

	suspend fun add(card: Card) = database.cardRepository.insert(card)

	suspend fun delete(card: Card) = database.cardRepository.delete(card)

	suspend fun update(card: Card) = database.cardRepository.update(card)

	fun getCountFlow(like: String = "") = database.cardRepository.getCountFlow(like)

	fun getCountFlow() = database.cardRepository.getCountFlow()

	fun getCardFlow(like: String = "", number: Int) = database.cardRepository.getCardFlow(like, number)

	fun getByIdFlow(id: Int) = database.cardRepository.getByIdFlow(id)

	suspend fun getById(id: Int) = database.cardRepository.getById(id)

	fun getByModuleCardFlow(moduleCard: ModuleCard) = database.cardRepository.getByIdFlow(moduleCard.idCard)

	suspend fun getRandom() = database.cardRepository.getRandom()

	suspend fun getRandomWithout(id: Int) = database.cardRepository.getRandomWithOut(id)

}