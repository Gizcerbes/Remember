package com.uogames.repository.providers

import com.uogames.database.repository.ModuleCardRepository
import com.uogames.dto.local.Module
import com.uogames.dto.local.ModuleCard
import com.uogames.map.ModuleCardMap.toGlobal
import com.uogames.map.ModuleCardMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider

class ModuleCardProvider(
	private val dataProvider: DataProvider,
	private val mcr: ModuleCardRepository,
	private val network: NetworkProvider
) {

	suspend fun insert(moduleCard: ModuleCard) = mcr.insert(moduleCard)

	suspend fun delete(moduleCard: ModuleCard) = mcr.delete(moduleCard)

	suspend fun update(moduleCard: ModuleCard) = mcr.update(moduleCard)

	suspend fun getById(id: Int) = mcr.getById(id)

	fun getByModuleIdListFlow(id: Int) = mcr.getByModuleID(id)

	fun getCountByModuleIdFlow(id: Int) = mcr.getCountByModuleIdFlow(id)

	suspend fun getCountByModule(module: Module) = mcr.getCountByModuleId(module.id)

	suspend fun getByPositionOfModule(idModule: Int, position: Int) = mcr.getByPositionOfModule(idModule, position)

	fun getByModuleFlow(module: Module) = mcr.getByModule(module)

	suspend fun getRandom() = mcr.getRandomModule()

	suspend fun getRandomWithout(idModule: Int) = mcr.getRandomModuleWithout(idModule)

	suspend fun getRandom(idModule: Int) = mcr.getRandomModule(idModule)

	suspend fun getRandomWithout(idModule: Int, idCard: Int) = mcr.getRandomModuleWithout(idModule, idCard)

	suspend fun countGlobal(moduleGlobalId: Long) = network.moduleCard.count(moduleGlobalId)

	suspend fun getGlobal(moduleGlobalId: Long, number: Long) = network.moduleCard.get(moduleGlobalId, number)

	suspend fun getGlobalById(globalId: Long) = network.moduleCard.get(globalId)

	suspend fun getGlobalCount(moduleGlobalId: Long) = network.moduleCard.count(moduleGlobalId)

	suspend fun share(id: Int): ModuleCard? {
		val moduleCard = getById(id)
		return moduleCard?.let {
			val module = dataProvider.module.share(it.idModule)
			val card = dataProvider.cards.share(it.idCard)
			val res = network.moduleCard.post(it.toGlobal(module, card))
			val updatedModuleCard = it.update(res)
			update(updatedModuleCard)
			return@let updatedModuleCard
		}
	}

	suspend fun download(globalId: Long): ModuleCard? {
		val local = mcr.getByGlobalId(globalId)
		if (local == null) {
			val nmc = network.moduleCard.get(globalId)
			val module = dataProvider.module.download(nmc.idModule)
			val card = dataProvider.cards.download(nmc.idCard)
			return if (module != null && card != null) {
				val localId = insert(ModuleCard(idModule = module.id, idCard = card.id).update(nmc))
				mcr.getById(localId.toInt())
			} else {
				null
			}
		}
		return local
	}

}