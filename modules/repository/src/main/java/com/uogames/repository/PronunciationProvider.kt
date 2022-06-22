package com.uogames.repository

import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Image
import com.uogames.dto.Phrase
import com.uogames.dto.Pronunciation
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PronunciationProvider(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) : Provider() {

	fun addAsync(pronunciation: Pronunciation, bytes: ByteArray) = ioScope.async {
		val id = database.pronunciationRepository.insert(pronunciation).toInt()
		val uri = fileRepository.saveFile("$id.gpp", bytes)
		database.pronunciationRepository.update(Pronunciation(id,uri.toString()))
		return@async id
	}

	fun deleteAsync(pronunciation: Pronunciation) = ioScope.async {
		database.pronunciationRepository.getById(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.dataBase64.toUri())
			return@async database.pronunciationRepository.delete(pronunciation)
		} ?: false
	}

	fun updateAsync(pronunciation: Pronunciation, bytes: ByteArray) = ioScope.async {
		database.pronunciationRepository.getById(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.gpp", bytes)
			return@async database.pronunciationRepository.update(Pronunciation(pronunciation.id, uri.toString()))
		}?: false
	}

	fun getCount() = database.pronunciationRepository.countFlow()

	fun getById(id: Int) = database.pronunciationRepository.getById(id)

	fun getByNumber(number: Int) = database.pronunciationRepository.getByNumber(number)

	fun getByPhrase(phrase: Phrase) = database.pronunciationRepository.getByPhrase(phrase)

	suspend fun clear(){
		database.pronunciationRepository.freeId().forEach{
			fileRepository.deleteFile(it.dataBase64.toUri())
			database.pronunciationRepository.delete(it)
		}
	}

}