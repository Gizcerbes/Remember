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

	fun getByModuleID(id: Int) = mcr.getByModuleID(id)

	fun getCountByModuleID(id: Int) = mcr.getCountByModuleID(id)

	fun getByModule(module: Module) = mcr.getByModule(module)

	suspend fun getRandom() = mcr.getRandomModule()

	suspend fun getRandomWithout(idModule: Int) = mcr.getRandomModuleWithout(idModule)

	suspend fun getRandom(idModule: Int) = mcr.getRandomModule(idModule)

	suspend fun getRandomWithout(idModule: Int, idCard: Int) = mcr.getRandomModuleWithout(idModule, idCard)

	suspend fun countGlobal(moduleGlobalId: Long) = network.moduleCard.count(moduleGlobalId)

	suspend fun getGlobal(moduleGlobalId: Long, number: Long) = network.moduleCard.get(moduleGlobalId, number)

	suspend fun getGlobalById(globalId: Long) = network.moduleCard.get(globalId)

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

}