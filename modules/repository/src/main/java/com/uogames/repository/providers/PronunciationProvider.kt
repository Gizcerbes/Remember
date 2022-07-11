package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first

class PronunciationProvider(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) : Provider() {

	fun addAsync(bytes: ByteArray) = ioScope.async {
		val id = database.pronunciationRepository.insert(Pronunciation(0,"")).toInt()
		val uri = fileRepository.saveFile("$id.gpp", bytes)
		database.pronunciationRepository.update(Pronunciation(id, uri.toString()))
		return@async id
	}

	fun deleteAsync(pronunciation: Pronunciation) = ioScope.async {
		database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.audioUri.toUri())
			return@async database.pronunciationRepository.delete(pronunciation)
		} ?: false
	}

	fun updateAsync(pronunciation: Pronunciation, bytes: ByteArray) = ioScope.async {
		database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.gpp", bytes)
			return@async database.pronunciationRepository.update(Pronunciation(pronunciation.id, uri.toString()))
		} ?: false
	}

	fun getCount() = database.pronunciationRepository.countFlow()

	suspend fun getById(id: Int) = database.pronunciationRepository.getById(id)

	fun getByIdAsync(id: Int) = ioScope.async { getById(id) }

	fun getByIdAsync(id: suspend () -> Int?) = ioScope.async { id()?.let { getById(it) } }

	fun getByIdFlow(id: Int) = database.pronunciationRepository.getByIdFlow(id)

	fun getByNumber(number: Int) = database.pronunciationRepository.getByNumberFlow(number)

	fun getByPhrase(phrase: Phrase) = database.pronunciationRepository.getByPhrase(phrase)

	suspend fun clear() {
		database.pronunciationRepository.freeId().forEach {
			fileRepository.deleteFile(it.audioUri.toUri())
			database.pronunciationRepository.delete(it)
		}
	}

}