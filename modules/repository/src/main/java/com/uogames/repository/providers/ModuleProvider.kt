package com.uogames.repository.providers

import com.uogames.database.repository.ModuleRepository
import com.uogames.dto.local.Module
import com.uogames.map.ModuleMap.toGlobal
import com.uogames.map.ModuleMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider

class ModuleProvider(
	private val dataProvider: DataProvider,
	private val mr: ModuleRepository,
	private val network: NetworkProvider
) {

	suspend fun add(module: Module) = mr.insert(module)

	suspend fun delete(module: Module) = mr.delete(module)

	suspend fun update(module: Module) = mr.update(module)

	fun getList() = mr.getList()

	fun getCount() = mr.getCount()

	fun getCountLike(like: String) = mr.getCountLike(like)

	fun getListLike(like: String) = mr.getListLike(like)

	fun getByIdFlow(id: Int) = mr.getByIdFlow(id)

	suspend fun getById(id: Int) = mr.getById(id)

	suspend fun getByPosition(like: String, position: Int) = mr.getByPosition(like, position)

	suspend fun countGlobal(like: String) = network.module.count(like)

	suspend fun getGlobal(like: String, number: Long) = network.module.get(like, number)

	suspend fun getGlobalById(globalId: Long) = network.module.get(globalId)

	suspend fun share(id: Int): Module? {
		val module = getById(id)
		return module?.let {
			val res = network.module.post(it.toGlobal())
			val updatedModule = it.update(res)
			update(updatedModule)
			return@let updatedModule
		}
	}

	suspend fun download(globalId: Long): Module? {
		val local = mr.getByGlobalId(globalId)
		if (local == null) {
			val nm = network.module.get(globalId)
			val localId = add(Module().update(nm))
			return mr.getById(localId.toInt())
		}
		return local
	}

}