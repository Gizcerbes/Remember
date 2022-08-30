package com.uogames.database.repository

import com.uogames.database.dao.PronunciationDAO
import com.uogames.database.map.PronunciationMap.toDTO
import com.uogames.database.map.PronunciationMap.toEntity
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class PronunciationRepository(private val dao: PronunciationDAO) {

	suspend fun insert(pronunciation: Pronunciation) = dao.insert(pronunciation.toEntity())

	suspend fun delete(pronunciation: Pronunciation) = dao.delete(pronunciation.toEntity()) > 0

	suspend fun update(pronunciation: Pronunciation) = dao.update(pronunciation.toEntity()) > 0

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFlow()

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	suspend fun getByGlobalId(id: Long) = dao.getByGlobalId(id)?.toDTO()

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getByNumber(number: Int) = dao.getByNumber(number)?.toDTO()

	fun getByNumberFlow(number: Int) = dao.getByNumberFlow(number).map { it?.toDTO() }

	fun getByPhrase(phrase: Phrase) = phrase.idPronounce?.let {
		getByIdFlow(it)
	} ?: MutableStateFlow(null).asStateFlow()

	suspend fun freeId() = dao.freePronounce().map { it.toDTO() }

}