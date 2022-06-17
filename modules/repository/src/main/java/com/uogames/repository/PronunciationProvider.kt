package com.uogames.repository

import android.util.Log
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PronunciationProvider(
	private val database: DatabaseRepository
) : Provider() {

	init {
		ioScope.launch { Log.e("TAG", ": ${database.pronunciationRepository.countFlow().first()}", ) }
	}

	fun addAsync(pronunciation: Pronunciation) = ioScope.async { database.pronunciationRepository.insert(pronunciation) }

	fun deleteAsync(pronunciation: Pronunciation) = ioScope.async { database.pronunciationRepository.delete(pronunciation) }

	fun updateAsync(pronunciation: Pronunciation) = ioScope.async { database.pronunciationRepository.update(pronunciation) }

	fun getCount() = database.pronunciationRepository.countFlow()

	fun getById(id: Int) = database.pronunciationRepository.getById(id)

	fun getByNumber(number: Int) = database.pronunciationRepository.getByNumber(number)

	fun getByPhrase(phrase: Phrase) = database.pronunciationRepository.getByPhrase(phrase)

	fun cleanAsync() = ioScope.async { database.pronunciationRepository.clean() }
}