package com.uogames.database.repository

import com.uogames.database.dao.CacheDAO
import com.uogames.database.entity.CacheEntity
import kotlinx.coroutines.flow.map

class CacheRepository(
	private val dao: CacheDAO
) {

	suspend fun insert(cacheEntity: List<String>) = dao.insert(cacheEntity.map { CacheEntity(0, it) })

	suspend fun insert(cacheEntity: Map<Int,String>) = dao.insert(cacheEntity.let {
		val list = ArrayList<CacheEntity>()
		it.forEach { (t, u) -> list.add(CacheEntity(t,u))  }
		list
	} )

	suspend fun clean() = dao.clean()

	suspend fun get(position: Int) = dao.get(position)?.data

	suspend fun getById(id:Int) = dao.getById(id)?.data

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFlow()

	fun getAll() = dao.getAll().map { list -> list.map { it.data } }

}