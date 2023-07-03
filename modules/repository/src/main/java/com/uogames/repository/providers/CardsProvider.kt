package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.CardRepository
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalShare
import com.uogames.map.CardMap.toGlobal
import com.uogames.map.CardMap.toLocalCard
import com.uogames.map.CardMap.update
import com.uogames.repository.DataProvider
import java.util.*

class CardsProvider(
    private val dataProvider: DataProvider,
    private val repository: CardRepository,
    private val network: NetworkProvider
) {

    suspend fun add(card: LocalCard) = repository.insert(card)

    suspend fun delete(card: LocalCard) = repository.delete(card)

    suspend fun deleteFree() = repository.deleteFree()

    suspend fun update(card: LocalCard) = repository.update(card)

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
    ) = repository.get(like, langFirst, langSecond, countryFirst, countrySecond, newest, position)

    suspend fun getView(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = repository.getView(like, langFirst, langSecond, countryFirst, countrySecond, newest, position)

    suspend fun getViewByID(id: Int) = repository.getViewById(id)

    fun getCountFlow() = repository.getCountFlow()

    suspend fun getClues(text: String) = repository.getClues(text)

    fun getByIdFlow(id: Int) = repository.getByIdFlow(id)

    suspend fun getById(id: Int) = repository.getById(id)

    suspend fun getRandom() = repository.getRandom()

    suspend fun getRandomView() = repository.getRandomView()

    suspend fun getRandomViewWithout(cardIds: Array<Int>) = repository.getRandomViewWithout(cardIds)

    suspend fun getRandomViewWithoutPhrases(phraseIds: Array<Int>) = repository.getRandomViewWithoutPhrases(phraseIds)

    suspend fun getUnknowableView() = repository.getUnknowableView()

    suspend fun getConfusingView(idPhrase: Int) = repository.getConfusingView(idPhrase)

    suspend fun getConfusingViewWithout(idPhrase: Int, phraseIds: Array<Int>) = repository.getConfusingViewWithoutPhrases(idPhrase, phraseIds)

    suspend fun getRandomWithout(id: Int) = repository.getRandomWithOut(id)

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

    fun isChanged(id: Int) = repository.isChanged(id)

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
            val phrase = res.idPhrase.let { ph -> dataProvider.phrase.getByGlobalId(ph) ?: dataProvider.phrase.download(ph) }?.id ?: throw Exception("First phrase wasn't saved")
            val translate = res.idTranslate.let { tr -> dataProvider.phrase.getByGlobalId(tr) ?: dataProvider.phrase.download(tr) }?.id ?: throw Exception("Second phrase wasn't saved")
            val image = res.idImage?.let { im -> dataProvider.images.getByGlobalId(im) ?: dataProvider.images.download(im) }?.id
            val updatedCard = it.toLocalCard().update(res, phrase, translate, image)
            update(updatedCard)
            return updatedCard
        }
        return null
    }

    suspend fun download(globalId: UUID): LocalCard? {
        val local = repository.getByGlobalId(globalId)
        val nc = network.card.get(globalId)
        val phrase = dataProvider.phrase.download(nc.idPhrase)
        val translate = dataProvider.phrase.download(nc.idTranslate)
        val image = nc.idImage?.let { dataProvider.images.download(it) }
        return if (phrase != null && translate != null) {
            val localId = if (local != null) {
                update(local.update(nc, phrase.id, translate.id, image?.id))
                local.id
            } else {
                add(LocalCard().update(nc, phrase.id, translate.id, image?.id)).toInt()
            }
            repository.getById(localId)
        } else {
            null
        }
    }

    suspend fun save(view: GlobalCardView): LocalCard {
        val l1 = repository.getByGlobalId(view.globalId)
        if (l1 == null) {
            val localID = add(
                LocalCard(
                    idPhrase = view.phrase.let { dataProvider.phrase.save(it) }.id,
                    idTranslate = view.translate.let { dataProvider.phrase.save(it) }.id,
                    reason = view.reason,
                    idImage = view.image?.let { dataProvider.images.save(it) }?.id,
                    timeChange = view.timeChange,
                    like = view.like,
                    dislike = view.dislike,
                    globalId = view.globalId,
                    globalOwner = view.user.globalOwner
                )
            ).toInt()
            return repository.getById(localID) ?: throw Exception("Card wasn't saved")
        } else if (l1.timeChange <= view.timeChange) {
            val l2 = l1.update(
                view = view,
                idPhrase = view.phrase.let { dataProvider.phrase.save(it) }.id,
                idTranslate = view.translate.let { dataProvider.phrase.save(it) }.id,
                idImage = view.image?.let { dataProvider.images.save(it) }?.id,
            )
            update(l2)
            return l2
        } else {
            return l1
        }
    }

}