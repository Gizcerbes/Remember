package com.uogames.database

import android.content.Context
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.dto.Card

class DatabaseRepository private constructor(private val database: MyDatabase) {

    companion object {
        private var INSTANCE: DatabaseRepository? = null

        fun getINSTANCE(context: Context): DatabaseRepository {
            if (INSTANCE == null) {
                INSTANCE = DatabaseRepository(MyDatabase.get(context))
            }
            return INSTANCE as DatabaseRepository
        }
    }

    suspend fun addCard(card: Card) = database.cardDAO().insert(card.toEntity())

    suspend fun updateCard(card: Card) = database.cardDAO().update(card.toEntity())

    suspend fun deleteCard(card: Card) = database.cardDAO().delete(card.toEntity())

    suspend fun cardCount() = database.cardDAO().count()

    suspend fun cardCount(like: String) = database.cardDAO().count(like)

    suspend fun getCard(number: Long) = database.cardDAO().get(number)?.toDTO()

    suspend fun getCard(number: Long, like: String) = database.cardDAO().get(number, like)?.toDTO()

    suspend fun getCard(phrase: String, translate: String) = database.cardDAO().get(phrase, translate)?.toDTO()

    suspend fun getRandomCard() = database.cardDAO().getRandom()?.toDTO()

    suspend fun getRandomCardWithout(phrase: String) = database.cardDAO().getRandomWithout(phrase)?.toDTO()

    suspend fun getRandomCardWithout(phrase: String, translate: String) = database.cardDAO().getRandomWithout(phrase,translate)?.toDTO()

    suspend fun exists(card: Card) = database.cardDAO().exists(card.phrase, card.translate)


}