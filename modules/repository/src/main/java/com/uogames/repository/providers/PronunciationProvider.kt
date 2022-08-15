package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.flow.first

class PronunciationProvider(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) {

//	fun addAsync(bytes: ByteArray) = ioScope.async {
//		val id = database.pronunciationRepository.insert(Pronunciation(0,"")).toInt()
//		val uri = fileRepository.saveFile("$id.gpp", bytes)
//		database.pronunciationRepository.update(Pronunciation(id, uri.toString()))
//		return@async id
//	}

	suspend fun add(bytes: ByteArray): Int{
		val id = database.pronunciationRepository.insert(Pronunciation(0,"")).toInt()
		val uri = fileRepository.saveFile("$id.mp4", bytes)
		database.pronunciationRepository.update(Pronunciation(id, uri.toString()))
		return id
	}

//	fun deleteAsync(pronunciation: Pronunciation) = ioScope.async {
//		database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
//			fileRepository.deleteFile(it.audioUri.toUri())
//			return@async database.pronunciationRepository.delete(pronunciation)
//		} ?: false
//	}

	suspend fun delete(pronunciation: Pronunciation): Boolean{
		return database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.audioUri.toUri())
			return database.pronunciationRepository.delete(pronunciation)
		} ?: false
	}

//	fun updateAsync(pronunciation: Pronunciation, bytes: ByteArray) = ioScope.async {
//		database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
//			val uri = fileRepository.saveFile("${it.id}.gpp", bytes)
//			return@async database.pronunciationRepository.update(Pronunciation(pronunciation.id, uri.toString()))
//		} ?: false
//	}

	suspend fun update(pronunciation: Pronunciation, bytes: ByteArray): Boolean{
		return database.pronunciationRepository.getByIdFlow(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.gpp", bytes)
			return database.pronunciationRepository.update(Pronunciation(pronunciation.id, uri.toString()))
		} ?: false
	}

	fun getCount() = database.pronunciationRepository.countFlow()

	suspend fun getById(id: Int) = database.pronunciationRepository.getById(id)

	//fun getByIdAsync(id: Int) = ioScope.async { getById(id) }

	//fun getByIdAsync(id: suspend () -> Int?) = ioScope.async { id()?.let { getById(it) } }

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