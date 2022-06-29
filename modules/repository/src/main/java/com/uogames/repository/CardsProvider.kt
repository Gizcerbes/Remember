package com.uogames.repository

import android.util.Log
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CardsProvider(
	private val database: DatabaseRepository
) : Provider() {

	fun addAsync(card: Card) = ioScope.async { database.cardRepository.insert(card) }

	fun deleteAsync(card: Card) = ioScope.async { database.cardRepository.delete(card) }

	fun updateAsync(card: Card) = ioScope.async { database.cardRepository.update(card) }

	fun getCountFlow(like: String = "") = database.cardRepository.getCountFlow(like)

	fun getCardFlow(like: String = "", number: Int) = database.cardRepository.getCardFlow(like, number)

	fun getByIdFlow(id: Int) = database.cardRepository.getByIdFlow(id)

}