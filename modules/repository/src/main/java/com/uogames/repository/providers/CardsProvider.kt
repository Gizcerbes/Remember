package com.uogames.repository.providers

import com.uogames.database.repository.CardRepository
import com.uogames.dto.local.Card
import com.uogames.dto.local.ModuleCard
import com.uogames.map.CardMap.toGlobal
import com.uogames.map.CardMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider

class CardsProvider(
	private val dataProvider: DataProvider,
	private val repository: CardRepository,
	private val network: NetworkProvider
) {

	suspend fun add(card: Card) = repository.insert(card)

	suspend fun delete(card: Card) = repository.delete(card)

	suspend fun update(card: Card) = repository.update(card)

	suspend fun getCard(like: String = "", number: Int) = repository.getCard(like, number)

	fun getCountFlow(like: String = "") = repository.getCountFlow(like)

	fun getCountFlow() = repository.getCountFlow()

	fun getCardFlow(like: String = "", number: Int) = repository.getCardFlow(like, number)

	fun getByIdFlow(id: Int) = repository.getByIdFlow(id)

	suspend fun getById(id: Int) = repository.getById(id)

	fun getByModuleCardFlow(moduleCard: ModuleCard) = repository.getByIdFlow(moduleCard.idCard)

	suspend fun getRandom() = repository.getRandom()

	suspend fun getRandomWithout(id: Int) = repository.getRandomWithOut(id)

	suspend fun countGlobal(like: String) = network.card.count(like)

	suspend fun getGlobalById(globalId: Long) = network.card.get(globalId)

	suspend fun getGlobal(like: String, number: Long) = network.card.get(like, number)

	suspend fun share(id: Int): Card? {
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

	suspend fun download(globalId: Long): Card? {
		val local = repository.getByGlobalId(globalId)
		if (local == null) {
			val nc = network.card.get(globalId)
			val phrase = dataProvider.phrase.download(nc.idPhrase)
			val translate = dataProvider.phrase.download(nc.idTranslate)
			val image = nc.idImage?.let { dataProvider.images.download(it) }
			return if (phrase != null && translate != null) {
				val localId = add(Card().update(nc, phrase.id, translate.id, image?.id))
				repository.getById(localId.toInt())
			} else {
				null
			}
		}
		return local
	}

}