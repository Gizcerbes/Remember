package com.uogames.repository.providers

import com.uogames.database.repository.CacheRepository

class CacheProvider(
	private val rep: CacheRepository
) {

	suspend fun insert(data: List<String>) = rep.insert(data)

	suspend fun insert(data: Map<Int, String>) = rep.insert(data)

	suspend fun clean() = rep.clean()

	suspend fun get(position: Int) = rep.get(position)

	suspend fun getById(id:Int) = rep.getById(id)

	suspend fun count() = rep.count()

	fun countFlow() = rep.countFlow()

	fun getAll() = rep.getAll()

}