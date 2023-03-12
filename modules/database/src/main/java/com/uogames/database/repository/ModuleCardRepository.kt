package com.uogames.database.repository

import com.uogames.database.dao.ModuleCardDAO
import com.uogames.database.map.ModuleCardMap.toDTO
import com.uogames.database.map.ModuleCardMap.toEntity
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.ModuleCard
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleCardRepository(private val dao: ModuleCardDAO) {

	suspend fun insert(moduleCard: ModuleCard) = dao.insert(moduleCard.toEntity())

	suspend fun delete(moduleCard: ModuleCard) = dao.delete(moduleCard.toEntity()) > 0

	suspend fun update(moduleCard: ModuleCard) = dao.update(moduleCard.toEntity()) > 0

	fun getByModuleID(id: Int) = dao.getByModuleID(id).map { it.map { mc -> mc.toDTO() } }

	fun getByModule(module: LocalModule) = dao.getByModuleID(module.id).map { it.map { mc -> mc.toDTO() } }

	fun getCountByModuleIdFlow(id: Int) = dao.getCountByModuleIdFlow(id)

	suspend fun getCountByModuleId(id: Int) = dao.getCountByModuleId(id)

	suspend fun getByPositionOfModule(idModule: Int, position: Int) = dao.getByPositionOfModule(idModule, position)?.toDTO()

	suspend fun getRandomModule() = dao.getRandomModuleCard()?.toDTO()

	suspend fun getRandomModule(idModule: Int) = dao.getRandomModuleCard(idModule)?.toDTO()

	suspend fun getRandomModuleWithout(idCard: Int) = dao.getRandomWithout(idCard)?.toDTO()

	suspend fun getRandomModuleWithout(idModule: Int, idCard: Int) = dao.getRandomWithout(idModule, idCard)?.toDTO()

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	suspend fun getByGlobalId(globalId: UUID) = dao.getByGlobalId(globalId)?.toDTO()
	
	suspend fun removeByModuleId(idModule: Int) = dao.removeByModule(idModule)

}