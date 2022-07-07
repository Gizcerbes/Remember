package com.uogames.repository.providers

import com.uogames.database.repository.ModuleCardRepository
import com.uogames.dto.Module
import com.uogames.dto.ModuleCard
import kotlinx.coroutines.async

class ModuleCardProvider(
	private val mcr: ModuleCardRepository
) : Provider() {

	fun insertAsync(moduleCard: ModuleCard) = ioScope.async { mcr.insert(moduleCard) }

	fun deleteAsync(moduleCard: ModuleCard) = ioScope.async { mcr.delete(moduleCard) }

	fun updateAsync(moduleCard: ModuleCard) = ioScope.async { mcr.update(moduleCard) }

	fun getByModuleID(id: Int) = mcr.getByModuleID(id)

	fun getCountByModuleID(id: Int) = mcr.getCountByModuleID(id)

	fun getByModule(module: Module) = mcr.getByModule(module)

	fun getRandomAsync() = ioScope.async { mcr.getRandomModule() }

	fun getRandomWithoutAsync(idCard: Int) = ioScope.async { mcr.getRandomModuleWithout(idCard) }

	fun getRandomAsync(idModule: Int) = ioScope.async { mcr.getRandomModule(idModule) }

	fun getRandomWithoutAsync(idModule: Int, idCard: Int) = ioScope.async { mcr.getRandomModuleWithout(idModule, idCard) }
}