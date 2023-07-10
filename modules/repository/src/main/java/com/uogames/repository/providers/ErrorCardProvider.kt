package com.uogames.repository.providers

import com.uogames.database.repository.ErrorCardRepository
import com.uogames.dto.local.ErrorCard
import com.uogames.repository.DataProvider
import com.uogames.repository.map.ErrorCardMap.toDTO
import com.uogames.repository.map.ErrorCardMap.toEntity

class ErrorCardProvider(
    private val dataProvider: DataProvider,
    private val rep: ErrorCardRepository
) {

    suspend fun add(errorCard: ErrorCard) = rep.insert(errorCard.toEntity())

    suspend fun update(errorCard: ErrorCard) = rep.update(errorCard.toEntity())

    suspend fun delete(errorCard: ErrorCard) = rep.delete(errorCard.toEntity())

    suspend fun getByPhraseAndTranslate(
        idPhrase: Int,
        idTranslate: Int
    ) = rep.getByPhraseAndTranslate(idPhrase, idTranslate)?.toDTO()

}