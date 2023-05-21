package com.uogames.repository.providers

import com.uogames.database.repository.ErrorCardRepository
import com.uogames.dto.local.ErrorCard
import com.uogames.repository.DataProvider

class ErrorCardProvider(
    private val dataProvider: DataProvider,
    private val rep: ErrorCardRepository
) {

    suspend fun add(errorCard: ErrorCard) = rep.insert(errorCard)

    suspend fun update(errorCard: ErrorCard) = rep.update(errorCard)

    suspend fun delete(errorCard: ErrorCard) = rep.delete(errorCard)

    suspend fun getByPhraseAndTranslate(
        idPhrase: Int,
        idTranslate: Int
    ) = rep.getByPhraseAndTranslate(idPhrase, idTranslate)

}