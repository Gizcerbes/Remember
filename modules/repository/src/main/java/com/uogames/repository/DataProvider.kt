package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import kotlinx.coroutines.launch

class DataProvider private constructor(
    private val database: DatabaseRepository
) : Provider() {

    companion object {
        private var INSTANCE: DataProvider? = null


        fun get(context: Context): DataProvider {
            if (INSTANCE == null) INSTANCE = DataProvider(
                DatabaseRepository.getINSTANCE(context)
            )
            return INSTANCE as DataProvider
        }
    }

    fun getCard(number: Long, call: (Card) -> Unit) = ioScope.launch {
        call(database.getCard(number))
    }

    fun addCard(card: Card) = ioScope.launch {
        database.addCard(card)
    }

    fun updateCard(card: Card) = ioScope.launch {
        database.updateCard(card)
    }

    fun deleteCard(card: Card) = ioScope.launch {
        database.deleteCard(card)
    }

    fun cardCount(call: (Long) -> Unit) = ioScope.launch {
        call(database.cardCount())
    }
}