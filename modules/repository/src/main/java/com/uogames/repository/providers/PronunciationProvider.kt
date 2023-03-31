package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.PronunciationRepository
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.map.PronunciationMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.flow.first
import java.util.*

class PronunciationProvider(
	private val dataProvider: DataProvider,
	private val database: PronunciationRepository,
	private val fileRepository: FileRepository,
	private val network: NetworkProvider
) {

	suspend fun add(bytes: ByteArray): Int {
		val id = database.insert(LocalPronunciation(0, "")).toInt()
		val uri = fileRepository.saveFile("$id.mp4", bytes)
		database.update(LocalPronunciation(id, uri.toString()))
		return id
	}

	suspend fun delete(pronunciation: LocalPronunciation): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.audioUri.toUri())
			return database.delete(pronunciation)
		} ?: false
	}

	suspend fun update(pronunciation: LocalPronunciation, bytes: ByteArray): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.mp4", bytes)
			return database.update(LocalPronunciation(pronunciation.id, uri.toString()))
		} ?: false
	}

	fun load(pronunciation: LocalPronunciation): ByteArray? = fileRepository.readFile(pronunciation.audioUri.toUri())

	fun load(pv: LocalPronunciationView): ByteArray? = fileRepository.readFile(pv.audioUri.toUri())

	suspend fun getById(id: Int) = database.getById(id)

	fun getByIdFlow(id: Int) = database.getByIdFlow(id)

	fun getByPhrase(phrase: LocalPhrase) = database.getByPhrase(phrase)

	suspend fun getByGlobalId(id: UUID) = database.getByGlobalId(id)

	suspend fun getGlobalById(id: UUID) = network.pronounce.get(id)

	suspend fun getGlobalViewByID(id: UUID) = network.pronounce.getView(id)

	suspend fun downloadData(id: UUID) = network.pronounce.load(id)

	suspend fun clear() {
		database.freeId().forEach {
			fileRepository.deleteFile(it.audioUri.toUri())
			database.delete(it)
		}
	}

	suspend fun share(id: Int): LocalPronunciation? {
		val pronounce = getById(id)
		pronounce?.globalId?.let { if (network.pronounce.exists(it)) return pronounce }
		val res = pronounce?.let {
			fileRepository.readFile(it.audioUri.toUri())?.let { bytes -> network.pronounce.upload(bytes) }
		}?.let { LocalPronunciation(pronounce.id, pronounce.audioUri, it.globalId, it.globalOwner) }
		res?.let { database.update(it) }
		return res ?: pronounce
	}

	suspend fun download(id: UUID): LocalPronunciation? {
		val local = database.getByGlobalId(id)

		if (local == null) {
			val localId = add(network.pronounce.load(id))
			val l = database.getById(localId) ?: return null
			val new = l.update(network.pronounce.get(id))
			database.update(new)
			return new
		}
		return local
	}

	suspend fun save(view: GlobalPronunciationView): LocalPronunciation {
		val l1 = database.getByGlobalId(view.globalId)
		return if (l1 == null){
			val localID = add(network.pronounce.load(view.globalId))
			val l = database.getById(localID)?.update(view) ?: throw Exception("Pronunciation wasn't saved")
			database.update(l)
			l
		}else {
			l1
		}
	}

}
