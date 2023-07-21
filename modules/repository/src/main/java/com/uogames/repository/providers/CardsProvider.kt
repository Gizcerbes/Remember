package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.CardRepository
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.global.GlobalImageView
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalShare
import com.uogames.map.CardMap.toGlobal
import com.uogames.map.CardMap.toLocalCard
import com.uogames.map.CardMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.map.CardMap.toDTO
import com.uogames.repository.map.CardMap.toEntity
import com.uogames.repository.map.CardMap.toViewDTO
import com.uogames.repository.map.CardMap.update
import com.uogames.repository.map.PhraseViewMap.toDTO
import kotlinx.coroutines.flow.map
import java.util.*

class CardsProvider(
	private val dataProvider: DataProvider,
	private val repository: CardRepository,
	private val network: NetworkProvider
) {

	private val phraseBuilder: suspend (id:Int) -> LocalPhraseView = { dataProvider.phrase.getViewByID(it) ?: throw Exception("Phrase isn't saved") }
	private val imageBuilder: suspend (id: Int) -> LocalImageView? = { dataProvider.images.getViewById(it) }

	private val phraseUpdate: suspend (GlobalPhraseView) -> Int = { dataProvider.phrase.fastSave(it) }
	private val imageUpdate: suspend (GlobalImageView) -> Int = { dataProvider.images.fastSave(it) }

	suspend fun add(card: LocalCard) = repository.insert(card.toEntity())

	suspend fun delete(card: LocalCard) = repository.delete(card.toEntity())

	suspend fun deleteFree() = repository.deleteFree()

	suspend fun update(card: LocalCard) = repository.update(card.toEntity())

	suspend fun count(
		like: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null
	) = repository.count(like, langFirst, langSecond, countryFirst, countrySecond)

	suspend fun get(
		like: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = repository.get(like, langFirst, langSecond, countryFirst, countrySecond, newest, position)?.toDTO()

	suspend fun getView(
		like: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = repository.get(like, langFirst, langSecond, countryFirst, countrySecond, newest, position)?.toViewDTO(phraseBuilder, imageBuilder)

	suspend fun getListView(
		like: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		newest: Boolean = false,
		position: Int? = null,
		limit: Int = 1
	) = repository.getList(like, langFirst, langSecond, countryFirst, countrySecond, newest, position, limit)
		.map { it.toViewDTO(phraseBuilder, imageBuilder) }

	suspend fun getViewByID(id: Int) = repository.getViewById(id)?.toViewDTO(phraseBuilder, imageBuilder)

	fun getCountFlow() = repository.getCountFlow()

	suspend fun getClues(text: String) = repository.getClues(text)

	fun getByIdFlow(id: Int) = repository.getByIdFlow(id).map { it?.toDTO() }

	suspend fun getById(id: Int) = repository.getById(id)?.toDTO()

	fun existByGlobalIdFlow(id: UUID) = repository.existByGlobalId(id.toString())

	suspend fun getRandomView() = repository.getRandomView()?.toViewDTO(phraseBuilder, imageBuilder)

	suspend fun getRandomViewWithoutPhrases(phraseIds: Array<Int>) = repository.getRandomViewWithoutPhrases(phraseIds)?.toViewDTO(phraseBuilder, imageBuilder)

	suspend fun getUnknowableView() = repository.getUnknowableView()?.toViewDTO(phraseBuilder, imageBuilder)

	suspend fun getConfusingView(idPhrase: Int) = repository.getConfusingView(idPhrase)?.toViewDTO(phraseBuilder, imageBuilder)

	suspend fun getConfusingViewWithout(idPhrase: Int, phraseIds: Array<Int>) = repository.getConfusingViewWithoutPhrases(idPhrase, phraseIds)?.toViewDTO(phraseBuilder, imageBuilder)

	fun countFree() = repository.countFree()

	suspend fun countGlobal(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null
	) = network.card.count(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond
	)

	suspend fun getGlobalView(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		number: Long
	) = network.card.getView(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond,
		number = number
	)

	suspend fun getGlobalListView(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		number: Long,
		limit: Int
	) = network.card.getListView(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond,
		number = number,
		limit = limit
	)

	fun isChangedFlow(id: Int) = repository.isChangedFlow(id)

	suspend fun isChanged(id: Int) = repository.isChanged(id)

	suspend fun share(id: Int): LocalCard? {
		val card = getById(id)
		return card?.let {
			val phrase = dataProvider.phrase.share(it.idPhrase)
			val translate = dataProvider.phrase.share(it.idTranslate)
			val image = it.idImage?.let { img -> dataProvider.images.share(img) }
			val res = network.card.post(it.toGlobal(phrase, translate, image))
			val updatedCard = it.update(res)
			update(updatedCard)
			return@let updatedCard
		}
	}

	suspend fun addToShare(cv: LocalCardView) {
		dataProvider.phrase.addToShare(cv.phrase)
		dataProvider.phrase.addToShare(cv.translate)
		cv.image?.let { dataProvider.images.addToShare(it) }
		if (getById(cv.id)?.changed != true) return
		val exists = dataProvider.share.exists(idCard = cv.id)
		if (!exists) dataProvider.share.save(LocalShare(idCard = cv.id))
	}

	suspend fun shareV2(id: Int): LocalCard? {
		val card = getViewByID(id)
		card?.let {
			val p = card.phrase.globalId.apply { if (card.phrase.changed) dataProvider.phrase.shareV2(card.phrase.id) }
			val t = card.translate.globalId.apply { if (card.translate.changed) dataProvider.phrase.shareV2(card.translate.id) }
			val i = card.image?.globalId.apply { card.image?.id?.let { it1 -> dataProvider.images.shareV2(it1) } }
			val res = network.card.post(card.toLocalCard().toGlobal(p, t, i))
			val phrase = res.idPhrase.let { ph -> dataProvider.phrase.getByGlobalId(ph)?.toDTO() ?: dataProvider.phrase.download(ph) }?.id
				?: throw Exception("First phrase wasn't saved")
			val translate = res.idTranslate.let { tr -> dataProvider.phrase.getByGlobalId(tr)?.toDTO() ?: dataProvider.phrase.download(tr) }?.id
				?: throw Exception("Second phrase wasn't saved")
			val image = res.idImage?.let { im -> dataProvider.images.getByGlobalId(im) ?: dataProvider.images.download(im) }?.id
			val updatedCard = it.toLocalCard().update(res, phrase, translate, image)
			update(updatedCard)
			return updatedCard
		}
		return null
	}

	suspend fun download(globalId: UUID): LocalCard? {
		val local = repository.getByGlobalId(globalId.toString())
		val nc = network.card.get(globalId)
		val phrase = dataProvider.phrase.download(nc.idPhrase)
		val translate = dataProvider.phrase.download(nc.idTranslate)
		val image = nc.idImage?.let { dataProvider.images.download(it) }
		return if (phrase != null && translate != null) {
			val localId = if (local != null) {
				update(local.toDTO().update(nc, phrase.id, translate.id, image?.id))
				local.id
			} else {
				add(LocalCard().update(nc, phrase.id, translate.id, image?.id)).toInt()
			}
			repository.getById(localId)?.toDTO()
		} else {
			null
		}
	}

	suspend fun save(view: GlobalCardView): LocalCard {
		val l1 = repository.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			val localID = repository.insert(view.toEntity(phraseUpdate, imageUpdate)).toInt()
			repository.getById(localID)?.toDTO() ?: throw Exception("Card wasn't saved")
		} else if (l1.timeChange <= view.timeChange) {
			val up = l1.update(view, phraseUpdate, imageUpdate)
			repository.update(up)
			up.toDTO()
		} else {
			l1.toDTO()
		}
	}

	suspend fun fastSave(view: GlobalCardView): Int {
		val l1 = repository.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			repository.insert(view.toEntity(phraseUpdate, imageUpdate)).toInt()
		} else if (l1.timeChange <= view.timeChange) {
			repository.update(l1.update(view, phraseUpdate, imageUpdate))
			l1.id
		} else {
			l1.id
		}
	}

}