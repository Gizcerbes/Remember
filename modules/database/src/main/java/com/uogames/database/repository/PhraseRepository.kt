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

	fun countFlow() = dao.countFLOW()

	fun countFlow(like: String) = dao.countFlow(like)

	fun countFlow(like: String, lang: String) = dao.countFlow(like, lang)

	fun getFlow(position: Int) = dao.getFlow(position).map { it?.toDTO() }

	fun getFlow(like: String, position: Int) = dao.getFlow(like, position).map { it?.toDTO() }

	fun getFlow(like: String, lang: String, position: Int) = dao.getFlow(like, lang, position).map { it?.toDTO() }

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

	fun getListIdFlow(like: String, lang: String) = dao.getListIdFlow(like,lang)

	fun getListIdFlow(like: String) = dao.getListIdFlow(like)

	suspend fun exists(phrase: String) = dao.exists(phrase)


}