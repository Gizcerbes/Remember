package com.uogames.database.repository

import com.uogames.database.dao.ModuleCardDAO
import com.uogames.database.map.ModuleCardMap.toDTO
import com.uogames.database.map.ModuleCardMap.toEntity
import com.uogames.dto.local.Module
import com.uogames.dto.local.ModuleCard
import kotlinx.coroutines.flow.map

class ModuleCardRepository(private val moduleCardDAO: ModuleCardDAO) {

	suspend fun insert(moduleCard: ModuleCard) = moduleCardDAO.insert(moduleCard.toEntity())

	suspend fun delete(moduleCard: ModuleCard) = moduleCardDAO.delete(moduleCard.toEntity()) > 0

	suspend fun update(moduleCard: ModuleCard) = moduleCardDAO.update(moduleCard.toEntity()) > 0

	fun getByModuleID(id: Int) = moduleCardDAO.getByModuleID(id).map { it.map { mc -> mc.toDTO() } }

	fun getByModule(module: Module) = moduleCardDAO.getByModuleID(module.id).map { it.map { mc -> mc.toDTO() } }

	fun getCountByModuleIdFlow(id: Int) = moduleCardDAO.getCountByModuleIdFlow(id)

	suspend fun getCountByModuleId(id: Int) = moduleCardDAO.getCountByModuleId(id)

	suspend fun getByPositionOfModule(idModule: Int, position: Int) = moduleCardDAO.getByPositionOfModule(idModule, position)?.toDTO()

	suspend fun getRandomModule() = moduleCardDAO.getRandomModuleCard()?.toDTO()

	suspend fun getRandomModule(idModule: Int) = moduleCardDAO.getRandomModuleCard(idModule)?.toDTO()

	suspend fun getRandomModuleWithout(idCard: Int) = moduleCardDAO.getRandomWithout(idCard)?.toDTO()

	suspend fun getRandomModuleWithout(idModule: Int, idCard: Int) = moduleCardDAO.getRandomWithout(idModule, idCard)?.toDTO()

	suspend fun getById(id: Int) = moduleCardDAO.getById(id)?.toDTO()

	suspend fun getByGlobalId(globalId: Long) = moduleCardDAO.getByGlobalId(globalId)?.toDTO()

}