package com.uogames.repository.providers

import com.uogames.database.repository.ModuleRepository
import com.uogames.dto.local.Module

class ModuleProvider(
	private val mr: ModuleRepository,
) {

	suspend fun add(module: Module) = mr.insert(module)

	suspend fun delete(module: Module) = mr.delete(module)

	suspend fun update(module: Module) = mr.update(module)

	fun getList() = mr.getList()

	fun getCount() = mr.getCount()

	fun getListLike(like: String) = mr.getListLike(like)

	fun getByIdFlow(id: Int) = mr.getByIdFlow(id)

	suspend fun getById(id: Int) = mr.getById(id)

	//fun getByIdAsync(id: Int) = ioScope.async { getById(id) }

	//fun getByIdAsync(id: suspend () -> Int?) = ioScope.async { id()?.let { getById(it) }  }
}