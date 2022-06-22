package com.uogames.repository

import com.uogames.database.DatabaseRepository
import com.uogames.dto.Phrase
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.IDN

class PhraseProvider(
	private val database: DatabaseRepository
) : Provider() {

	fun addAsync(phrase: Phrase) = ioScope.async { database.phraseRepository.add(phrase) }

	fun add(phrase: Phrase, callId: (Long) -> Unit) = ioScope.launch {
		val id = addAsync(phrase).await()
		launch(Dispatchers.Main) { callId(id) }
	}

	fun deleteAsync(phrase: Phrase) = ioScope.async { database.phraseRepository.delete(phrase) }

	fun delete(phrase: Phrase, call: (Boolean) -> Unit) = ioScope.launch {
		val res = deleteAsync(phrase).await()
		launch(Dispatchers.Main) { call(res) }
	}

	fun updateAsync(phrase: Phrase) = ioScope.async { database.phraseRepository.update(phrase) }

	fun update(phrase: Phrase, call: (Boolean) -> Unit) = ioScope.launch {
		val res = updateAsync(phrase).await()
		launch(Dispatchers.Main) { call(res) }
	}

	fun countFlow() = database.phraseRepository.countFlow()

	fun countFlow(like: String) = database.phraseRepository.countFlow(like)

	fun countFlow(like: String, lang: String) = database.phraseRepository.countFlow(like, lang)

	fun getFlow(position: Int) = database.phraseRepository.getFlow(position)

	fun getFlow(like: String, position: Int) = database.phraseRepository.getFlow(like, position)

	fun getFlow(like: String, lang: String, position: Int) = database.phraseRepository.getFlow(like, lang, position)

	fun getByIdFlow(id: Int) = database.phraseRepository.getByIdFlow(id)

	fun existsAsync(phrase: String) = ioScope.async { database.phraseRepository.exists(phrase) }

	fun exists(phrase: String, call: (Boolean) -> Unit) = ioScope.launch {
		val res = existsAsync(phrase).await()
		launch(Dispatchers.Main) { call(res) }
	}
}