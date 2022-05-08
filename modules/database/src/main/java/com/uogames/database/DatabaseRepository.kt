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

    suspend fun getCard(number: Long) = database.cardDAO().get(number).toDTO()

}