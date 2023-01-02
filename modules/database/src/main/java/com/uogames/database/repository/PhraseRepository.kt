package com.uogames.database.repository

import com.uogames.database.dao.PhraseDAO
import com.uogames.database.map.PhraseMap.toDTO
import com.uogames.database.map.PhraseMap.toEntity
import com.uogames.dto.local.LocalPhrase
import kotlinx.coroutines.flow.map
import java.util.*

class PhraseRepository(private val dao: PhraseDAO) {

	suspend fun add(phrase: LocalPhrase) = dao.insert(phrase.toEntity())

	suspend fun delete(phrase: LocalPhrase) = dao.delete(phrase.toEntity()) > 0

	suspend fun update(phrase: LocalPhrase) = dao.update(phrase.toEntity()) > 0

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFLOW()

	suspend fun count(like: String) = dao.count(like)

	fun countFlow(like: String) = dao.countFlow(like)

	suspend fun count(like: String, lang: String) = dao.count(like, lang)

	fun countFlow(like: String, lang: String) = dao.countFlow(like, lang)

	fun countFlow(like: String, lang: String, country: String) = dao.countFlow(like, lang, country)

	suspend fun get(position: Int) = dao.get(position)?.toDTO()

	fun getFlow(position: Int) = dao.getFlow(position).map { it?.toDTO() }

	suspend fun get(like: String, position: Int) = dao.get(like, position)?.toDTO()

	fun getFlow(like: String, position: Int) = dao.getFlow(like, position).map { it?.toDTO() }

	suspend fun get(like: String, lang: String, position: Int) = dao.get(like, lang, position)?.toDTO()

	fun getFlow(like: String, lang: String, position: Int) = dao.getFlow(like, lang, position).map { it?.toDTO() }

	suspend fun get(like: String, lang: String, country: String, position: Int) = dao.get(like, lang, country, position)?.toDTO()

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)?.toDTO()

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getListId(like: String, lang: String) = dao.getListId(like, lang)

	fun getListIdFlow(like: String, lang: String) = dao.getListIdFlow(like, lang)

	suspend fun getListId(like: String) = dao.getListId(like)

	fun getListIdFlow(like: String) = dao.getListIdFlow(like)

	suspend fun exists(phrase: String) = dao.exists(phrase)


}