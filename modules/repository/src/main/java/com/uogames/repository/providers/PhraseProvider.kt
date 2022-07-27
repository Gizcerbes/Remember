package com.uogames.repository.providers

import com.uogames.database.DatabaseRepository
import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.Phrase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PhraseProvider(
	private val pr: PhraseRepository
) {
	suspend fun add(phrase: Phrase) = pr.add(phrase)

	suspend fun delete(phrase: Phrase) = pr.delete(phrase)

	suspend fun update(phrase: Phrase) = pr.update(phrase)

	fun countFlow() = pr.countFlow()

	fun countFlow(like: String) = pr.countFlow(like)

	fun countFlow(like: String, lang: String) = pr.countFlow(like, lang)

	fun getFlow(position: Int) = pr.getFlow(position)

	fun getFlow(like: String, position: Int) = pr.getFlow(like, position)

	fun getFlow(like: String, lang: String, position: Int) = pr.getFlow(like, lang, position)

	suspend fun getById(id: Int) = pr.getById(id)

	fun getByIdFlow(id: Int) = pr.getByIdFlow(id)

	suspend fun exists(phrase: String) = pr.exists(phrase)

	fun getListId(like: String, lang: String) = pr.getListIdFlow(like, lang)

	fun getListId(like: String) = pr.getListIdFlow(like)

}