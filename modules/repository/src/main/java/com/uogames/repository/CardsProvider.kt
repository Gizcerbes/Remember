package com.uogames.repository

import android.util.Log
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CardsProvider(
    private val database: DatabaseRepository
) : Provider() {

    fun getCard(number: Long, call: suspend (Card) -> Unit) = ioScope.launch {
        call(database.getCard(number) ?: Card())
    }

    fun getCard(number: Long, like: String, call: (Card) -> Unit) = ioScope.launch {
        call(database.getCard(number, like) ?: Card())
    }

    fun getCard(phrase: String, translate: String, call: (Card) -> Unit) = ioScope.launch {
        call(database.getCard(phrase, translate) ?: Card())
    }

    fun getCardAsync(number: Long, like: String) = ioScope.async {
        return@async database.getCard(number, like)
    }

    fun getRandomCard(call: (Card) -> Unit) = ioScope.launch {
        call(database.getRandomCard() ?: Card())
    }

    fun getRandomCardAsync() = ioScope.async {
        database.getRandomCard()
    }

    fun getRandomCardWithout(phrase: String, call: (Card) -> Unit) = ioScope.launch {
        call(getRandomCardWithoutAsync(phrase).await() ?: Card())
    }

    fun getRandomCardWithoutAsync(phrase: String) = ioScope.async {
        database.getRandomCardWithout(phrase)
    }

    fun getRandomCardWithout(phrase: String, translate: String, call: (Card) -> Unit) =
        ioScope.launch { call(getRandomCardWithoutAsync(phrase, translate).await() ?: Card()) }

    fun getRandomCardWithoutAsync(phrase: String, translate: String) = ioScope.async {
        database.getRandomCardWithout(phrase, translate)
    }

    fun addCard(card: Card) = ioScope.launch {
        database.addCard(card)
    }

    fun addCardAsync(card: Card) = ioScope.async {
        if (database.exists(card)) return@async false
        try {
            database.addCard(card)
            return@async true
        } catch (e: Exception) {
            return@async false
        }
    }

    fun updateCard(card: Card) = ioScope.launch {
        database.updateCard(card)
    }

    fun deleteCard(card: Card) = ioScope.launch {
        database.deleteCard(card)
    }

    fun deleteCardAsync(card: Card) = ioScope.async {
        if (!database.exists(card)) return@async false
        try {
            database.deleteCard(card)
            return@async !database.exists(card)
        } catch (e: Exception) {
            return@async false
        }
    }

    fun cardCount(call: (Long) -> Unit) = ioScope.launch {
        call(database.cardCount())
    }

    fun cardCount(like: String, call: (Int) -> Unit) = ioScope.launch {
        call(database.cardCount(like))
    }

    fun cardCountAsync(like: String) = ioScope.async {
        database.cardCount(like)
    }

}