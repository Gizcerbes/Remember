package com.uogames.database.repository

import com.uogames.database.dao.PronunciationDAO
import com.uogames.database.entity.PhraseEntity
import com.uogames.database.entity.PronunciationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class PronunciationRepository(
	private val dao: PronunciationDAO,
) {

	suspend fun insert(pronunciation: PronunciationEntity) = dao.insert(pronunciation)

	suspend fun delete(pronunciation: PronunciationEntity) = dao.delete(pronunciation) > 0

	suspend fun update(pronunciation: PronunciationEntity) = dao.update(pronunciation) > 0

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFlow()

	suspend fun getById(id: Int) = dao.getById(id)

	suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)

	suspend fun getViewById(id: Int) = dao.getById(id)

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id)

	fun getByPhrase(phrase: PhraseEntity) = phrase.idPronounce?.let {
		getByIdFlow(it)
	} ?: MutableStateFlow(null).asStateFlow()

	suspend fun freeId() = dao.freePronounce()

}