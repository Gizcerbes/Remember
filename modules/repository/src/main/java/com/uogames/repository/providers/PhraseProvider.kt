package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalShare
import com.uogames.map.PhraseMap.toGlobal
import com.uogames.map.PhraseMap.toLocalPhrase
import com.uogames.map.PhraseMap.update
import com.uogames.repository.DataProvider
import java.util.*

class PhraseProvider(
    private val dataProvider: DataProvider,
    private val pr: PhraseRepository,
    private val network: NetworkProvider
) {
    suspend fun add(phrase: LocalPhrase) = pr.add(phrase)

    suspend fun delete(phrase: LocalPhrase) = pr.delete(phrase)

    suspend fun update(phrase: LocalPhrase) = pr.update(phrase)

    suspend fun get(like: String?, lang: String?, country: String?, newest: Boolean, position: Int?) =
        pr.get(like, lang, country, newest, position)

    suspend fun getView(like: String?, lang: String?, country: String?, newest: Boolean, position: Int?) =
        pr.getView(like, lang, country, newest, position)

    suspend fun getViewByID(id: Int) = pr.getViewById(id)

    fun countFlow() = pr.countFlow()

    suspend fun count(like: String?, lang: String?, country: String?) = pr.count(like, lang, country)

    suspend fun getById(id: Int) = pr.getById(id)

    fun getByIdFlow(id: Int) = pr.getByIdFlow(id)

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

    fun isChanged(id: Int) = pr.isChanged(id)

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
        if(getById(pv.id)?.changed != true) return
        val exists = dataProvider.share.exists(idPhrase = pv.id)
        if (!exists) dataProvider.share.save(LocalShare(idPhrase = pv.id))
    }

    suspend fun shareV2(id: Int): LocalPhrase? {
        val phrase = getViewByID(id)
        phrase?.let {
            val im = phrase.image?.let { img -> img.globalId ?: dataProvider.images.share(img.id)?.globalId ?: throw Exception("Image wasn't saved") }
            val pr = phrase.pronounce?.let { pro ->
                pro.globalId ?: dataProvider.pronounce.share(pro.id)?.globalId ?: throw Exception("Pronunciation wasn't sawed")
            }
            val res = network.phrase.post(phrase.toLocalPhrase().toGlobal(im,pr))
            val updatedPhrase = it.toLocalPhrase().update(res)
            update(updatedPhrase)
            return updatedPhrase
        }
        return null
    }

    suspend fun download(id: UUID): LocalPhrase? {
        val local = pr.getByGlobalId(id)
        val np = network.phrase.get(id)
        val image = np.idImage?.let { dataProvider.images.download(it) }
        val pronounce = np.idPronounce?.let { dataProvider.pronounce.download(it) }
        val localId = if (local != null) {
            update(local.update(network.phrase.get(id), pronounce?.id, image?.id))
            local.id
        } else {
            add(LocalPhrase().update(network.phrase.get(id), pronounce?.id, image?.id)).toInt()
        }
        return pr.getById(localId)
    }

    suspend fun save(view: GlobalPhraseView): LocalPhrase {
        val l1 = pr.getByGlobalId(view.globalId)
        if (l1 == null) {
            val localID = add(
                LocalPhrase(
                    phrase = view.phrase,
                    definition = view.definition,
                    lang = view.lang,
                    country = view.country,
                    idPronounce = view.pronounce?.let { dataProvider.pronounce.save(it) }?.id,
                    idImage = view.image?.let { dataProvider.images.save(it) }?.id,
                    timeChange = view.timeChange,
                    like = view.like,
                    dislike = view.dislike,
                    globalId = view.globalId,
                    globalOwner = view.user.globalOwner
                )
            ).toInt()
            return pr.getById(localID) ?: throw Exception("Phrase wasn't saved")
        } else if (l1.timeChange <= view.timeChange) {
            val l2 = l1.update(
                view = view,
                idPronounce = view.pronounce?.let { dataProvider.pronounce.save(it) }?.id,
                idImage = view.image?.let { dataProvider.images.save(it) }?.id
            )
            update(l2)
            return l2
        } else {
            return l1
        }
    }

}