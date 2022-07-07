package com.uogames.database.repository

import com.uogames.database.dao.PhraseDAO
import com.uogames.database.map.PhraseMap.toDTO
import com.uogames.database.map.PhraseMap.toEntity
import com.uogames.dto.Phrase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.IDN

class PhraseRepository(private val dao: PhraseDAO) {

	suspend fun add(phrase: Phrase) = dao.insert(phrase.toEntity())

	suspend fun delete(phrase: Phrase) = dao.delete(phrase.toEntity()) > 0

	suspend fun update(phrase: Phrase) = dao.update(phrase.toEntity()) > 0

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFLOW()

	suspend fun count(like: String) = dao.count(like)

	fun countFlow(like: String) = dao.countFlow(like)

	suspend fun count(like: String, lang: String) = dao.count(like, lang)

	fun countFlow(like: String, lang: String) = dao.countFlow(like, lang)

	suspend fun get(position: Int) = dao.get(position)?.toDTO()

	fun getFlow(position: Int) = dao.getFlow(position).map { it?.toDTO() }

	suspend fun get(like: String, position: Int) = dao.get(like, position)?.toDTO()

	fun getFlow(like: String, position: Int) = dao.getFlow(like, position).map { it?.toDTO() }

	suspend fun get(like: String, lang: String, position: Int) = dao.get(like, lang, position)?.toDTO()

	fun getFlow(like: String, lang: String, position: Int) = dao.getFlow(like, lang, position).map { it?.toDTO() }

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getListId(like: String, lang: String) = dao.getListId(like, lang)

	fun getListIdFlow(like: String, lang: String) = dao.getListIdFlow(like, lang)

	suspend fun getListId(like: String) = dao.getListId(like)

	fun getListIdFlow(like: String) = dao.getListIdFlow(like)

	suspend fun exists(phrase: String) = dao.exists(phrase)


}