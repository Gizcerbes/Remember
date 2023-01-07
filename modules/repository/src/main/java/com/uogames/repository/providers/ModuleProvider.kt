package com.uogames.repository.providers

import com.uogames.database.repository.ModuleRepository
import com.uogames.dto.local.LocalModule
import com.uogames.map.ModuleMap.toGlobal
import com.uogames.map.ModuleMap.update
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider
import java.util.*

class ModuleProvider(
	private val dataProvider: DataProvider,
	private val mr: ModuleRepository,
	private val network: NetworkProvider
) {

	suspend fun add(module: LocalModule) = mr.insert(module)

	suspend fun delete(module: LocalModule) = mr.delete(module)

	suspend fun update(module: LocalModule) = mr.update(module)

	suspend fun count(text: String?) = mr.count(text)

	suspend fun get(text: String?, position: Int) = mr.get(text, position)


	fun getCount() = mr.getCount()

	fun getCountLike(like: String) = mr.getCountLike(like)

	fun getByIdFlow(id: Int) = mr.getByIdFlow(id)

	suspend fun getById(id: Int) = mr.getById(id)

	suspend fun getByPosition(like: String, position: Int) = mr.getByPosition(like, position)

	suspend fun countGlobal(like: String) = network.module.count(like)

	suspend fun getGlobal(like: String, number: Long) = network.module.get(like, number)

	suspend fun getGlobalById(globalId: UUID) = network.module.get(globalId)

	suspend fun share(id: Int): LocalModule? {
		val module = getById(id)
		return module?.let {
			val res = network.module.post(it.toGlobal())
			val updatedModule = it.update(res)
			update(updatedModule)
			return@let updatedModule
		}
	}

	suspend fun download(globalId: UUID): LocalModule? {
		val local = mr.getByGlobalId(globalId)
		val nm = network.module.get(globalId)
		val localId = if (local != null) {
			update(local.update(nm))
			dataProvider.moduleCard.removeByModule(local.id)
			local.id
		} else {
			add(LocalModule().update(nm)).toInt()
		}
		return mr.getById(localId)
	}

}