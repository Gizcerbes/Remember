package com.uogames.database.repository

import com.uogames.database.dao.ErrorCardDAO
import com.uogames.database.map.ErrorCardMap.toDTO
import com.uogames.database.map.ErrorCardMap.toEntity
import com.uogames.dto.ErrorCard

class ErrorCardRepository(private val dao: ErrorCardDAO) {

	suspend fun insert(errorCard: ErrorCard) = dao.insert(errorCard.toEntity())

	suspend fun update(errorCard: ErrorCard) = dao.update(errorCard.toEntity())

	suspend fun delete(errorCard: ErrorCard) = dao.delete(errorCard.toEntity())

	suspend fun countByPhraseId(idPhrase: Int) = dao.countByPhraseId(idPhrase)

	suspend fun getByPhraseId(idPhrase: Int, number: Long) = dao.getByPhraseId(idPhrase, number)?.toDTO()

	suspend fun getByPhraseAndTranslate(idPhrase: Int, idTranslate: Int) = dao.getByPhraseAndTranslate(idPhrase, idTranslate)?.toDTO()

}