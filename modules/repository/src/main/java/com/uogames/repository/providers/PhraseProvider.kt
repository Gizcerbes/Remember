package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.PhraseRepository
import com.uogames.dto.local.LocalPhrase
import com.uogames.map.PhraseMap.toGlobal
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

    suspend fun get(like: String?, lang: String?, country: String?, position: Int?) =
        pr.get(like, lang, country, position)

    fun countFlow() = pr.countFlow()

    suspend fun count(like: String?, lang: String?, country: String?) =
        pr.count(like, lang, country)

    suspend fun getById(id: Int) = pr.getById(id)

    fun getByIdFlow(id: Int) = pr.getByIdFlow(id)

    suspend fun getByGlobalId(globalId: UUID) = pr.getByGlobalId(globalId)

    suspend fun countGlobal(
        text: String? = null,
        lang: String? = null,
        country: String? = null
    ) = network.phrase.count(
        text = text,
        lang = lang,
        country = country
    )

    suspend fun getGlobal(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long
    ) = network.phrase.get(
        text = text,
        lang = lang,
        country = country,
        number = number
    )

    suspend fun getGlobalById(globalId: UUID) = network.phrase.get(globalId)

    suspend fun share(id: Int): LocalPhrase? {
        val phrase = getById(id)
        return phrase?.let {
            val image = it.idImage?.let { image -> dataProvider.images.share(image) }
            val pronounce =
                it.idPronounce?.let { pronounce -> dataProvider.pronounce.share(pronounce) }
            val globalPhrase = it.toGlobal(image, pronounce)
            val res = network.phrase.post(globalPhrase)
            val updatedPhrase = it.update(res)
            update(updatedPhrase)
            return@let updatedPhrase
        }
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

}