package com.uogames.database.repository

import com.uogames.database.dao.PronunciationDAO
import com.uogames.database.map.PronunciationMap.toDTO
import com.uogames.database.map.PronunciationMap.toEntity
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class PronunciationRepository(val dao: PronunciationDAO) {

	suspend fun insert(pronunciation: Pronunciation) = dao.insert(pronunciation.toEntity())

	suspend fun delete(pronunciation: Pronunciation) = dao.delete(pronunciation.toEntity()) > 0

	suspend fun update(pronunciation: Pronunciation) = dao.update(pronunciation.toEntity()) > 0

	fun countFlow() = dao.countFlow()

	fun getById(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	fun getByNumber(number: Int) = dao.getByNumber(number).map { it?.toDTO() }

	fun getByPhrase(phrase: Phrase) = phrase.idPronounce?.let {
		getById(it)
	} ?: MutableStateFlow(null).asStateFlow()

	suspend fun clean() = dao.clean()

}