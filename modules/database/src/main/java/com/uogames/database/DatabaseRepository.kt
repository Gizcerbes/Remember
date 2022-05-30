package com.uogames.database

import android.content.Context
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.dto.Card
import kotlinx.coroutines.flow.map

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

	fun cardCountFlo(like: String) = database.cardDAO().countFLOW(like)

	fun getCardFLOW(number: Int, like: String) = database.cardDAO().getCardFLOW(number, like).map { it?.toDTO() }

    suspend fun getRandomCard() = database.cardDAO().getRandom()?.toDTO()

    suspend fun getRandomCardWithout(phrase: String, translate: String) = database.cardDAO().getRandomWithout(phrase,translate)?.toDTO()

    suspend fun exists(card: Card) = database.cardDAO().exists(card.phrase, card.translate)


}