package com.uogames.database.repository

import com.uogames.database.dao.ModuleDAO
import com.uogames.database.map.ModuleMap.toDTO
import com.uogames.database.map.ModuleMap.toEntity
import com.uogames.dto.local.Module
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleRepository(private val moduleDAO: ModuleDAO) {

	suspend fun insert(module: Module) = moduleDAO.insert(module.toEntity())

	suspend fun delete(module: Module) = moduleDAO.delete(module.toEntity()) > 0

	suspend fun update(module: Module) = moduleDAO.update(module.toEntity()) > 0

	fun getCount() = moduleDAO.getCount()

	fun getCountLike(like: String) = moduleDAO.getCountLike(like)

	fun getList() = moduleDAO.getList().map { it.map { module -> module.toDTO() } }

	fun getListLike(like: String) = moduleDAO.getListLike(like).map { it.map { module -> module.toDTO() } }

	suspend fun getById(id: Int) = moduleDAO.getById(id)?.toDTO()

	suspend fun getByGlobalId(globalId: UUID) = moduleDAO.getByGlobalId(globalId)?.toDTO()

	suspend fun getByPosition(like: String, position: Int) = moduleDAO.getByPosition(like, position)?.toDTO()

	fun getByIdFlow(id: Int) = moduleDAO.getByIdFlow(id).map { it?.toDTO() }

}