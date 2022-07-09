package com.uogames.repository.providers

import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import com.uogames.dto.ModuleCard
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first

class CardsProvider(
	private val database: DatabaseRepository
) : Provider() {

	fun addAsync(card: Card) = ioScope.async { database.cardRepository.insert(card) }

	fun deleteAsync(card: Card) = ioScope.async { database.cardRepository.delete(card) }

	fun updateAsync(card: Card) = ioScope.async { database.cardRepository.update(card) }

	fun getCountFlow(like: String = "") = database.cardRepository.getCountFlow(like)

	fun getCountFlow() = database.cardRepository.getCountFlow()

	fun getCardFlow(like: String = "", number: Int) = database.cardRepository.getCardFlow(like, number)

	fun getByIdFlow(id: Int) = database.cardRepository.getByIdFlow(id)

	suspend fun getById(id: Int) = database.cardRepository.getById(id)

	fun getByIdAsync(id: Int) = ioScope.async { database.cardRepository.getByIdFlow(id).first() }

	fun getByIdAsync(id: suspend () -> Int?) = ioScope.async { id()?.let { getById(it) } }

	fun getByModuleCard(moduleCard: ModuleCard) = database.cardRepository.getByIdFlow(moduleCard.idCard)

	fun getRandomAsync() = ioScope.async { database.cardRepository.getRandom() }

	fun getRandomWithoutAsync(id: Int) = ioScope.async { database.cardRepository.getRandomWithOut(id) }

}