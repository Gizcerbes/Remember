package com.uogames.database.repository

import com.uogames.database.dao.ErrorCardDAO
import com.uogames.database.entity.ErrorCardEntity

class ErrorCardRepository(private val dao: ErrorCardDAO) {

	suspend fun insert(errorCard: ErrorCardEntity) = dao.insert(errorCard)

	suspend fun update(errorCard: ErrorCardEntity) = dao.update(errorCard)

	suspend fun delete(errorCard: ErrorCardEntity) = dao.delete(errorCard)

	suspend fun getByPhraseAndTranslate(idPhrase: Int, idTranslate: Int) = dao.getByPhraseAndTranslate(idPhrase, idTranslate)

}