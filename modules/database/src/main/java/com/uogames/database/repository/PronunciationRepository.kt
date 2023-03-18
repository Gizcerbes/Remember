package com.uogames.database.repository

import com.uogames.database.dao.PronunciationDAO
import com.uogames.database.entity.PronunciationEntity
import com.uogames.database.map.PronunciationMap.toDTO
import com.uogames.database.map.PronunciationMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.dto.local.LocalPronunciationView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.*

class PronunciationRepository(
	private val dao: PronunciationDAO,
	private val map: ViewMap<PronunciationEntity, LocalPronunciationView>
	) {

	suspend fun insert(pronunciation: LocalPronunciation) = dao.insert(pronunciation.toEntity())

	suspend fun delete(pronunciation: LocalPronunciation) = dao.delete(pronunciation.toEntity()) > 0

	suspend fun update(pronunciation: LocalPronunciation) = dao.update(pronunciation.toEntity()) > 0

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFlow()

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)?.toDTO()

	suspend fun getViewById(id: Int) = dao.getById(id)?.let { map.toDTO(it) }

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getByNumber(number: Int) = dao.getByNumber(number)?.toDTO()

	fun getByNumberFlow(number: Int) = dao.getByNumberFlow(number).map { it?.toDTO() }

	fun getByPhrase(phrase: LocalPhrase) = phrase.idPronounce?.let {
		getByIdFlow(it)
	} ?: MutableStateFlow(null).asStateFlow()

	suspend fun freeId() = dao.freePronounce().map { it.toDTO() }

}