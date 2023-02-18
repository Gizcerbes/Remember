package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.ModuleCardRepository
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.ModuleCard
import com.uogames.map.ModuleCardMap.toGlobal
import com.uogames.map.ModuleCardMap.update
import com.uogames.repository.DataProvider
import java.util.*

class ModuleCardProvider(
	private val dataProvider: DataProvider,
	private val mcr: ModuleCardRepository,
	private val network: NetworkProvider
) {

	suspend fun insert(moduleCard: ModuleCard) = mcr.insert(moduleCard)

	suspend fun delete(moduleCard: ModuleCard) = mcr.delete(moduleCard)

	suspend fun update(moduleCard: ModuleCard) = mcr.update(moduleCard)

	suspend fun getById(id: Int) = mcr.getById(id)

	fun getCountByModuleIdFlow(id: Int) = mcr.getCountByModuleIdFlow(id)

	suspend fun getCountByModule(module: LocalModule) = mcr.getCountByModuleId(module.id)

	suspend fun getByPositionOfModule(idModule: Int, position: Int) = mcr.getByPositionOfModule(idModule, position)

	fun getByModuleFlow(module: LocalModule) = mcr.getByModule(module)

	suspend fun getRandom(idModule: Int) = mcr.getRandomModule(idModule)

	suspend fun getRandomWithout(idModule: Int, idCard: Int) = mcr.getRandomModuleWithout(idModule, idCard)

	suspend fun removeByModule(idModule: Int) = mcr.removeByModuleId(idModule)

	suspend fun countGlobal(moduleGlobalId: UUID) = network.moduleCard.count(moduleGlobalId)

	suspend fun getGlobal(moduleGlobalId: UUID, number: Long) = network.moduleCard.get(moduleGlobalId, number)

	suspend fun getGlobalById(globalId: UUID) = network.moduleCard.get(globalId)

	suspend fun getGlobalCount(moduleGlobalId: UUID) = network.moduleCard.count(moduleGlobalId)

	suspend fun share(id: Int): ModuleCard? {
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

	suspend fun download(module: LocalModule, position: Long): ModuleCard? {
		val globalModuleId = module.globalId ?: return null
		val nmc = network.moduleCard.get(globalModuleId, position)
		val card = dataProvider.cards.download(nmc.idCard) ?: return null
		val id = insert(ModuleCard(idModule = module.id, idCard = card.id).update(nmc))
		return getById(id.toInt())
	}

}