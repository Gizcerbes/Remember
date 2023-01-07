package com.uogames.repository.providers

import com.uogames.database.repository.CardRepository
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.ModuleCard
import com.uogames.map.CardMap.toGlobal
import com.uogames.map.CardMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider
import java.util.*

class CardsProvider(
    private val dataProvider: DataProvider,
    private val repository: CardRepository,
    private val network: NetworkProvider
) {

    suspend fun add(card: LocalCard) = repository.insert(card)

    suspend fun delete(card: LocalCard) = repository.delete(card)

    suspend fun update(card: LocalCard) = repository.update(card)

    //suspend fun getCard(like: String = "", number: Int) = repository.getCard(like, number)
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
        position: Int? = null
    ) = repository.get(like, langFirst, langSecond, countryFirst, countrySecond, position)

    fun getCountFlow(like: String = "") = repository.getCountFlow()

    fun getCountFlow() = repository.getCountFlow()

   // fun getCardFlow(like: String = "", number: Int) = repository.getCardFlow(like, number)

    fun getByIdFlow(id: Int) = repository.getByIdFlow(id)

    suspend fun getById(id: Int) = repository.getById(id)

   // fun getByModuleCardFlow(moduleCard: ModuleCard) = repository.getByIdFlow(moduleCard.idCard)

    suspend fun getRandom() = repository.getRandom()

    suspend fun getRandomWithout(id: Int) = repository.getRandomWithOut(id)

    suspend fun getByGlobalId(globalId: UUID) = repository.getByGlobalId(globalId)

    suspend fun countGlobal(like: String) = network.card.count(like)

  //  suspend fun getGlobalById(globalId: UUID) = network.card.get(globalId)

    suspend fun getGlobal(like: String, number: Long) = network.card.get(like, number)

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

}