package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.database.repository.PronunciationRepository
import com.uogames.dto.local.Phrase
import com.uogames.dto.local.Pronunciation
import com.uogames.network.NetworkProvider
import com.uogames.network.provider.PronunciationProvider
import com.uogames.repository.DataProvider
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.flow.first

class PronunciationProvider(
	private val dataProvider: DataProvider,
	private val database: PronunciationRepository,
	private val fileRepository: FileRepository,
	private val network: NetworkProvider
) {

	suspend fun add(bytes: ByteArray): Int {
		val id = database.insert(Pronunciation(0, "")).toInt()
		val uri = fileRepository.saveFile("$id.mp4", bytes)
		database.update(Pronunciation(id, uri.toString()))
		return id
	}

	suspend fun delete(pronunciation: Pronunciation): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.audioUri.toUri())
			return database.delete(pronunciation)
		} ?: false
	}

	suspend fun update(pronunciation: Pronunciation, bytes: ByteArray): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.gpp", bytes)
			return database.update(Pronunciation(pronunciation.id, uri.toString()))
		} ?: false
	}

	fun getCount() = database.countFlow()

	suspend fun getById(id: Int) = database.getById(id)

	fun getByIdFlow(id: Int) = database.getByIdFlow(id)

	fun getByNumber(number: Int) = database.getByNumberFlow(number)

	fun getByPhrase(phrase: Phrase) = database.getByPhrase(phrase)

	suspend fun getByGlobalId(id: Long) = network.pronounce.get(id)

	suspend fun clear() {
		database.freeId().forEach {
			fileRepository.deleteFile(it.audioUri.toUri())
			database.delete(it)
		}
	}

	suspend fun share(id: Int): Pronunciation? {
		val pronounce = getById(id)
		val res = pronounce?.let {
			if (it.globalId == null) it
			else null
		}?.let {
			fileRepository.readFile(it.audioUri.toUri())?.let { bytes -> network.pronounce.upload(bytes) }
		}?.let { Pronunciation(pronounce.id, pronounce.audioUri, it.globalId, it.globalOwner) }
		res?.let { database.update(it) }
		return res ?: pronounce
	}
}
