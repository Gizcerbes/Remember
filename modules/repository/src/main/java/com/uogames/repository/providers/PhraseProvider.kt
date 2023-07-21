package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.global.GlobalImageView
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.global.GlobalPronunciationView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciationView
import com.uogames.dto.local.LocalShare
import com.uogames.map.PhraseMap.toGlobal
import com.uogames.map.PhraseMap.toLocalPhrase
import com.uogames.map.PhraseMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.map.PhraseViewMap.toDTO
import com.uogames.repository.map.PhraseViewMap.toEntity
import com.uogames.repository.map.PhraseViewMap.toViewDTO
import com.uogames.repository.map.PhraseViewMap.update
import com.uogames.repository.map.PronounceMap.toDTO
import java.util.*

class PhraseProvider(
	private val dataProvider: DataProvider,
	private val pr: PhraseRepository,
	private val network: NetworkProvider
) {

	private val pronounceBuilder: suspend (id: Int) -> LocalPronunciationView? = { dataProvider.pronounce.getViewById(it) }
	private val imageBuilder: suspend (id: Int) -> LocalImageView? = { dataProvider.images.getViewById(it) }

	private val pronounceUpdate: suspend (pv: GlobalPronunciationView) -> Int? = { dataProvider.pronounce.fastSave(it) }
	private val imageUpdate: suspend (iv: GlobalImageView) -> Int? = { dataProvider.images.fastSave(it) }


	suspend fun insert(phrase: LocalPhrase) = pr.insert(phrase.toEntity())

	suspend fun delete(phrase: LocalPhrase) = pr.delete(phrase.toEntity())

	suspend fun update(phrase: LocalPhrase) = pr.update(phrase.toEntity())

	suspend fun get(like: String?, lang: String?, country: String?, newest: Boolean, position: Int?) =
		pr.get(like, lang, country, newest, position)?.toDTO()

	suspend fun getView(like: String?, lang: String?, country: String?, newest: Boolean, position: Int?) =
		pr.get(like, lang, country, newest, position)?.toViewDTO(pronounceBuilder, imageBuilder)

	suspend fun getListView(
		like: String?,
		lang: String?,
		country: String?,
		newest: Boolean,
		offset: Int?,
		limit: Int = 1
	) = pr.getList(like, lang, country, newest, offset, limit).map { it.toViewDTO(pronounceBuilder, imageBuilder) }

	suspend fun getById(id: Int) = pr.getById(id)?.toDTO()

	suspend fun getViewByID(id: Int) = pr.getById(id)?.toViewDTO(pronounceBuilder, imageBuilder)

	fun countFlow() = pr.countFlow()

	suspend fun count(like: String?, lang: String?, country: String?) = pr.count(like, lang, country)

	suspend fun getByGlobalId(id: UUID) = pr.getByGlobalId(id.toString())

	fun isExistsByGlobalId(id: UUID) = pr.isExistsByGlobalIdFlow(id.toString())

	fun countFree() = pr.countFree()

	suspend fun deleteFree() = pr.deleteFree()

	suspend fun countGlobal(
		text: String? = null,
		lang: String? = null,
		country: String? = null
	) = network.phrase.count(
		text = text,
		lang = lang,
		country = country
	)

	suspend fun getGlobalView(
		text: String? = null,
		lang: String? = null,
		country: String? = null,
		number: Long
	) = network.phrase.getView(
		text = text,
		lang = lang,
		country = country,
		number = number
	)

	suspend fun getGlobalListView(
		text: String? = null,
		lang: String? = null,
		country: String? = null,
		number: Long,
		limit: Int = 1
	) = network.phrase.getListView(
		text = text,
		lang = lang,
		country = country,
		number = number,
		limit = limit
	)

	fun isChangedFlow(id: Int) = pr.isChangedFlow(id)

	suspend fun isChanged(id: Int) = pr.isChanged(id)

	suspend fun share(id: Int): LocalPhrase? {
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

	suspend fun addToShare(pv: LocalPhraseView) {
		pv.image?.let { dataProvider.images.addToShare(it) }
		pv.pronounce?.let { dataProvider.pronounce.adToShare(it) }
		if (getById(pv.id)?.changed != true) return
		val exists = dataProvider.share.exists(idPhrase = pv.id)
		if (!exists) dataProvider.share.save(LocalShare(idPhrase = pv.id))
	}

	suspend fun shareV2(id: Int): LocalPhrase? {
		val phrase = getViewByID(id)
		phrase?.let {
			val im = phrase.image?.apply { if (globalOwner == null) dataProvider.images.shareV2(id) }?.globalId
			val pr = phrase.pronounce?.apply { if (globalOwner == null) dataProvider.pronounce.shareV2(id) }?.globalId
			val res = network.phrase.post(phrase.toLocalPhrase().toGlobal(im, pr))
			val idImage = res.idImage?.let { it1 -> dataProvider.images.getByGlobalId(it1) ?: dataProvider.images.download(it1) }?.id
			val idPronounce =
				res.idPronounce?.let { it1 -> dataProvider.pronounce.getByGlobalId(it1)?.toDTO() ?: dataProvider.pronounce.download(it1) }?.id
			val updatedPhrase = it.toLocalPhrase().update(res, idPronounce, idImage)
			update(updatedPhrase)
			return updatedPhrase
		}
		return null
	}

	suspend fun download(id: UUID): LocalPhrase? {
		val local = pr.getByGlobalId(id.toString())?.toDTO()
		val np = network.phrase.get(id)
		val image = np.idImage?.let { dataProvider.images.download(it) }
		val pronounce = np.idPronounce?.let { dataProvider.pronounce.download(it) }
		val localId = if (local != null) {
			update(local.update(network.phrase.get(id), pronounce?.id, image?.id))
			local.id
		} else {
			insert(LocalPhrase().update(network.phrase.get(id), pronounce?.id, image?.id)).toInt()
		}
		return pr.getById(localId)?.toDTO()
	}

	suspend fun save(view: GlobalPhraseView): LocalPhrase {
		val l1 = pr.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			val localID = pr.insert(view.toEntity(pronounceUpdate, imageUpdate)).toInt()
			pr.getById(localID)?.toDTO() ?: throw Exception("Phrase wasn't saved")
		} else if (l1.timeChange <= view.timeChange) {
			val l2 = l1.update(view, pronounceUpdate, imageUpdate)
			pr.update(l2)
			l2.toDTO()
		} else {
			l1.toDTO()
		}
	}

	suspend fun fastSave(view: GlobalPhraseView): Int {
		val l1 = pr.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			pr.insert(view.toEntity(pronounceUpdate, imageUpdate)).toInt()
		} else if (l1.timeChange <= view.timeChange) {
			pr.update(l1.update(view, pronounceUpdate, imageUpdate))
			l1.id
		} else {
			l1.id
		}
	}

}