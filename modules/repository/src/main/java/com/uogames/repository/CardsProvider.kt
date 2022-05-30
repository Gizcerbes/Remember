package com.uogames.repository

import android.util.Log
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CardsProvider(
	private val database: DatabaseRepository
) : Provider() {

	fun getRandomCardAsync() = ioScope.async {
		database.getRandomCard()
	}

	fun getRandomCardWithoutAsync(phrase: String, translate: String) = ioScope.async {
		database.getRandomCardWithout(phrase, translate)
	}

	fun addCardAsync(card: Card) = ioScope.async {
		if (database.exists(card)) return@async false
		return@async try {
			database.addCard(card)
			true
		} catch (e: Exception) {
			false
		}
	}

	fun updateCard(card: Card) = ioScope.launch {
		database.updateCard(card)
	}

	fun deleteCardAsync(card: Card) = ioScope.async {
		if (!database.exists(card)) return@async false
		return@async try {
			database.deleteCard(card)
			!database.exists(card)
		} catch (e: Exception) {
			false
		}
	}


	fun getCardCountFlow(like: String) = database.cardCountFlo(like)

	fun getCardFlow(number: Int, like: String) = database.getCardFLOW(number, like)

}