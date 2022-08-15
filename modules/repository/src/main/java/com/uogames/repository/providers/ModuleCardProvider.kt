package com.uogames.repository.providers

import com.uogames.database.repository.ModuleCardRepository
import com.uogames.dto.local.Module
import com.uogames.dto.local.ModuleCard

class ModuleCardProvider(
	private val mcr: ModuleCardRepository
) {

	suspend fun insert(moduleCard: ModuleCard) = mcr.insert(moduleCard)

	suspend fun delete(moduleCard: ModuleCard) = mcr.delete(moduleCard)

	suspend fun update(moduleCard: ModuleCard) = mcr.update(moduleCard)

	fun getByModuleID(id: Int) = mcr.getByModuleID(id)

	fun getCountByModuleID(id: Int) = mcr.getCountByModuleID(id)

	fun getByModule(module: Module) = mcr.getByModule(module)

	suspend fun getRandom() = mcr.getRandomModule()

	suspend fun getRandomWithout(idModule: Int) = mcr.getRandomModuleWithout(idModule)

	suspend fun getRandom(idModule: Int) = mcr.getRandomModule(idModule)

	suspend fun getRandomWithout(idModule: Int, idCard: Int) = mcr.getRandomModuleWithout(idModule, idCard)
}