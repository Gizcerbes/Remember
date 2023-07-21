package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.ModuleCardRepository
import com.uogames.dto.global.GlobalModuleCardView
import com.uogames.dto.local.LocalCardView
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalModuleCardView
import com.uogames.dto.local.LocalModuleView
import com.uogames.dto.local.LocalShare
import com.uogames.map.ModuleCardMap.toGlobal
import com.uogames.map.ModuleCardMap.toLocalModuleCard
import com.uogames.map.ModuleCardMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.map.ModuleCardMap.toDTO
import com.uogames.repository.map.ModuleCardMap.toEntity
import com.uogames.repository.map.ModuleCardMap.toViewDTO
import com.uogames.repository.map.ModuleMap.toEntity
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleCardProvider(
	private val dataProvider: DataProvider,
	private val mcr: ModuleCardRepository,
	private val network: NetworkProvider
) {

	private val moduleBuilder: suspend (id: Int) -> LocalModuleView = { dataProvider.module.getViewById(it) ?: throw Exception("") }
	private val cardBuilder: suspend (id: Int) -> LocalCardView = { dataProvider.cards.getViewByID(it) ?: throw Exception("Cards isn't saved") }

	suspend fun insert(moduleCard: LocalModuleCard) = mcr.insert(moduleCard.toEntity())

	suspend fun delete(moduleCard: LocalModuleCard) = mcr.delete(moduleCard.toEntity())

	suspend fun update(moduleCard: LocalModuleCard) = mcr.update(moduleCard.toEntity())

	suspend fun getById(id: Int) = mcr.getById(id)?.toDTO()

	fun getCountByModuleIdFlow(id: Int) = mcr.getCountByModuleIdFlow(id)

	suspend fun getCountByModule(module: LocalModule) = mcr.getCountByModuleId(module.id)

	suspend fun getCountByModuleId(idModule: Int) = mcr.getCountByModuleId(idModule)

	suspend fun getView(idModule: Int, position: Int) = mcr.getViewByPositionOfModule(idModule, position)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun getViewByID(id: Int) = mcr.getViewById(id)?.toViewDTO(moduleBuilder, cardBuilder)

	fun getByModuleFlow(module: LocalModule) = mcr.getByModule(module.toEntity()).map { list -> list.map { it.toDTO() } }

	suspend fun getRandomModuleView(idModule: Int) = mcr.getRandomModuleView(idModule)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun getUnknowableView(idModule: Int) = mcr.getUnknowableView(idModule)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun getConfusingView(idModule: Int, idPhrase: Int) = mcr.getConfusingView(idModule, idPhrase)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun getConfusingViewWithout(idModule: Int, idPhrase: Int, phraseIds: Array<Int>) =
		mcr.getConfusingWithoutPhrases(idModule, idPhrase, phraseIds)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun getRandomViewWithoutPhrases(idModule: Int, phraseIds: Array<Int>) =
		mcr.getRandomModuleViewWithoutPhrases(idModule, phraseIds)?.toViewDTO(moduleBuilder, cardBuilder)

	suspend fun removeByModule(idModule: Int) = mcr.removeByModuleId(idModule)

	suspend fun getGlobalCount(moduleGlobalId: UUID) = network.moduleCard.count(moduleGlobalId)

	suspend fun getGlobalView(moduleID: UUID, number: Long) = network.moduleCard.getView(moduleID, number)

	suspend fun getGlobalListView(moduleGlobalId: UUID, number: Long, limit: Int) = network.moduleCard.getListView(moduleGlobalId, number, limit)

	suspend fun share(id: Int): LocalModuleCard? {
		val moduleCard = getById(id)
		return moduleCard?.let {
			val m = dataProvider.module.getById(moduleCard.idModule)
			val module = if (m?.globalId == null) dataProvider.module.share(it.idModule) else m
			val card = dataProvider.cards.share(it.idCard)
			val res = network.moduleCard.post(it.toGlobal(module, card))
			val updatedModuleCard = it.update(res)
			update(updatedModuleCard)
			return@let updatedModuleCard
		}
	}

	suspend fun addToShare(mcv: LocalModuleCardView) {
		dataProvider.cards.addToShare(mcv.card)
		//if (mcv.globalId == null) dataProvider.share.save(LocalShare(idModuleCard = mcv.id))
		dataProvider.share.save(LocalShare(idModuleCard = mcv.id, idModule = mcv.module.id))
	}

	suspend fun shareV2(id: Int): LocalModuleCard? {
		val moduleCard = getViewByID(id)
		moduleCard?.let {
			val gmID = it.module.globalId.apply { if (moduleCard.module.changed) dataProvider.module.share(moduleCard.module.id) }
			val cID = it.card.globalId.apply { if (moduleCard.card.changed) dataProvider.cards.shareV2(moduleCard.card.id) }
			val res = network.moduleCard.post(it.toLocalModuleCard().toGlobal(gmID, cID))
			val updatedModuleCard = it.toLocalModuleCard().update(res)
			update(updatedModuleCard)
			return updatedModuleCard
		}
		return null
	}

	suspend fun download(module: LocalModule, position: Long): LocalModuleCard? {
		val globalModuleId = module.globalId
		val nmc = network.moduleCard.get(globalModuleId, position)
		val card = dataProvider.cards.download(nmc.idCard) ?: return null
		val id = insert(LocalModuleCard(idModule = module.id, idCard = card.id).update(nmc))
		return getById(id.toInt())
	}

	suspend fun save(view: GlobalModuleCardView, module: LocalModule): LocalModuleCard {
		val l1 = mcr.getByGlobalId(view.globalId.toString())
		return if (l1 == null) {
			val localID = insert(
				LocalModuleCard(
					idModule = module.id,
					idCard = view.card.let { dataProvider.cards.save(it) }.id,
					globalId = view.globalId,
					globalOwner = view.user.globalOwner
				)
			).toInt()
			getById(localID) ?: throw Exception("ModuleCard wasn't saved")
		} else {
			view.card.let { dataProvider.cards.save(it) }
			l1.toDTO()
		}
	}

	suspend fun fastSave(view: GlobalModuleCardView, module: LocalModule): Long {
		return insert(
			LocalModuleCard(
				idModule = module.id,
				idCard = view.card.let { dataProvider.cards.fastSave(it) },
				globalId = view.globalId,
				globalOwner = view.user.globalOwner
			)
		)
	}


	suspend fun save(module: LocalModule) {
		module.globalId.let { moduleID ->
			val count = getGlobalCount(moduleID)
			for (number in 0 until count) save(getGlobalView(moduleID, number), module)
		}
	}

}