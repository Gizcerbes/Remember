package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.clientApi.version3.network.response.PronunciationResponse
import com.uogames.database.repository.PronunciationRepository
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.dto.local.LocalShare
import com.uogames.map.PronunciationMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.map.PhraseViewMap.toEntity
import com.uogames.repository.map.PronounceMap.toDTO
import com.uogames.repository.map.PronounceMap.toEntity
import com.uogames.repository.map.PronounceMap.toViewDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class PronunciationProvider(
	private val dataProvider: DataProvider,
	private val database: PronunciationRepository,
	private val fileRepository: FileRepository,
	private val network: NetworkProvider
) {

	suspend fun add(bytes: ByteArray): Int {
		val id = database.insert(LocalPronunciation().toEntity()).toInt()
		val uri = fileRepository.saveFile("$id.mp4", bytes)
		database.update(LocalPronunciation(id, uri.toString()).toEntity())
		return id
	}

	suspend fun delete(pronunciation: LocalPronunciation): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			fileRepository.deleteFile(it.audioUri.toUri())
			return database.delete(pronunciation.toEntity())
		} ?: false
	}

	suspend fun update(pronunciation: LocalPronunciation, bytes: ByteArray): Boolean {
		return database.getByIdFlow(pronunciation.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.mp4", bytes)
			return database.update(LocalPronunciation(pronunciation.id, uri.toString()).toEntity())
		} ?: false
	}

	fun load(pronunciation: LocalPronunciation): ByteArray? = fileRepository.readFile(pronunciation.audioUri.toUri())

	fun load(pv: LocalPronunciationView): ByteArray? = fileRepository.readFile(pv.audioUri.toUri())

	suspend fun getById(id: Int) = database.getById(id)?.toDTO()

	suspend fun getViewById(id: Int) = database.getViewById(id)?.toViewDTO()


	suspend fun getByGlobalId(id: UUID) = database.getByGlobalId(id.toString())


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
		res?.let { database.update(it.toEntity()) }
		return res ?: pronounce
	}

	suspend fun shareV2(id: Int): LocalPronunciation? {
		val pron = getById(id)
		pron?.globalId?.let {
			if (network.pronounce.exists(it)) return pron
		}
		val res = pron?.let {
			fileRepository.readFile(pron.audioUri.toUri())?.let {
				network.pronounce.uploadV2(
					byteArray = it,
					PronunciationResponse(pron.globalId, audioUri = "")
				)
			}
		}?.let { LocalPronunciation(pron.id, pron.audioUri, it.globalId, it.globalOwner) }
		res?.let { database.update(it.toEntity()) }
		return res ?: pron
	}

	suspend fun adToShare(pv: LocalPronunciationView) {
		val r = dataProvider.share.exists(idPronounce = pv.id)
		if (!r) dataProvider.share.save(LocalShare(idPronounce = pv.id))
	}

	suspend fun download(id: UUID): LocalPronunciation? {
		val local = database.getByGlobalId(id.toString())
		if (local == null) {
			val localId = add(network.pronounce.load(id))
			val l = database.getById(localId)?.toDTO() ?: return null
			val new = l.update(network.pronounce.get(id))
			database.update(new.toEntity())
			return new
		}
		return local.toDTO()
	}

	suspend fun save(view: GlobalPronunciationView): LocalPronunciation {
		val l1 = database.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			val localID = add(network.pronounce.load(view.globalId))
			val l = database.getById(localID)?.toDTO()?.update(view) ?: throw Exception("Pronunciation wasn't saved")
			database.update(l.toEntity())
			l
		} else {
			l1.toDTO()
		}
	}

	suspend fun fastSave(view: GlobalPronunciationView): Int {
		val l1 = database.getByGlobalId(view.globalId.toString())
		return l1?.id ?: add(network.pronounce.load(view.globalId))
	}

}
