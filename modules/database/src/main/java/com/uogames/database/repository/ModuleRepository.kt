package com.uogames.database.repository

import com.uogames.database.dao.ModuleDAO
import com.uogames.database.map.ModuleMap.toDTO
import com.uogames.database.map.ModuleMap.toEntity
import com.uogames.dto.local.Module
import kotlinx.coroutines.flow.map

class ModuleRepository(private val moduleDAO: ModuleDAO) {

	suspend fun insert(module: Module) = moduleDAO.insert(module.toEntity())

	suspend fun delete(module: Module) = moduleDAO.delete(module.toEntity()) > 0

	suspend fun update(module: Module) = moduleDAO.update(module.toEntity()) > 0

	fun getCount() = moduleDAO.getCount()

	fun getList() = moduleDAO.getList().map { it.map { module -> module.toDTO() } }

	fun getListLike(like: String) = moduleDAO.getListLike(like).map { it.map { module -> module.toDTO() } }

	suspend fun getById(id: Int) = moduleDAO.getById(id)?.toDTO()

	fun getByIdFlow(id: Int) = moduleDAO.getByIdFlow(id).map { it?.toDTO() }

}