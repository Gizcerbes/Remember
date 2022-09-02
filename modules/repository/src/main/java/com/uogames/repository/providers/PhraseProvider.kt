package com.uogames.repository.providers

import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.local.Phrase
import com.uogames.map.PhraseMap.toGlobal
import com.uogames.map.PhraseMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider
import java.util.*

class PhraseProvider(
	private val dataProvider: DataProvider,
	private val pr: PhraseRepository,
	private val network: NetworkProvider
) {
	suspend fun add(phrase: Phrase) = pr.add(phrase)

	suspend fun delete(phrase: Phrase) = pr.delete(phrase)

	suspend fun update(phrase: Phrase) = pr.update(phrase)

	suspend fun get(like: String, position: Int) = pr.get(like, position)

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

	suspend fun countGlobal(like: String) = network.phrase.count(like)

	suspend fun getGlobal(like: String, number: Long) = network.phrase.get(like, number)

	suspend fun getGlobalById(globalId: UUID) = network.phrase.get(globalId)

	suspend fun share(id: Int): Phrase? {
		val phrase = getById(id)
		return phrase?.let {
			val image = it.idImage?.let { image -> dataProvider.images.share(image) }
			val pronounce = it.idPronounce?.let { pronounce -> dataProvider.pronounce.share(pronounce) }
			val globalPhrase = it.toGlobal(image, pronounce)
			val res = network.phrase.post(globalPhrase)
			val updatedPhrase = it.update(res)
			update(updatedPhrase)
			return@let updatedPhrase
		}
	}

	suspend fun download(id: UUID): Phrase? {
		val local = pr.getByGlobalId(id)
		if (local == null) {
			val np = network.phrase.get(id)
			val image = np.idImage?.let { dataProvider.images.download(it) }
			val phrase = np.idPronounce?.let { dataProvider.pronounce.download(it) }
			val localId = add(Phrase().update(network.phrase.get(id), image?.id, phrase?.id))
			return pr.getById(localId.toInt())
		}
		return local
	}

}