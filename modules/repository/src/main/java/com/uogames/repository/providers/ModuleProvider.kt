package com.uogames.repository.providers

import com.uogames.database.repository.ModuleRepository
import com.uogames.dto.Module
import com.uogames.dto.ModuleCard
import kotlinx.coroutines.async

class ModuleProvider(
	private val mr: ModuleRepository,
) : Provider() {


	fun addAsync(module: Module) = ioScope.async { mr.insert(module) }

	fun deleteAsync(module: Module) = ioScope.async { mr.delete(module) }

	fun updateAsync(module: Module) = ioScope.async { mr.update(module) }

	fun getList() = mr.getList()

	fun getCount() = mr.getCount()

	fun getListLike(like: String) = mr.getListLike(like)

	fun getByIdFlow(id: Int) = mr.getByIdFlow(id)

	suspend fun getById(id: Int) = mr.getById(id)

}