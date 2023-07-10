package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.repository.ModuleRepository
import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import com.uogames.dto.local.LocalShare
import com.uogames.map.ModuleMap.toGlobal
import com.uogames.map.ModuleMap.update
import com.uogames.repository.DataProvider
import com.uogames.repository.map.ModuleMap.toDTO
import com.uogames.repository.map.ModuleMap.toEntity
import com.uogames.repository.map.ModuleMap.toViewDTO
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleProvider(
	private val dataProvider: DataProvider,
	private val mr: ModuleRepository,
	private val network: NetworkProvider
) {

	suspend fun add(module: LocalModule) = mr.insert(module.toEntity())

	suspend fun delete(module: LocalModule) = mr.delete(module.toEntity())

	suspend fun update(module: LocalModule) = mr.update(module.toEntity())

	suspend fun count(
		text: String? = null,
		fLang: String? = null,
		sLang: String? = null,
		fCountry: String? = null,
		sCountry: String? = null
	) = mr.count(text, fLang, sLang, fCountry, sCountry)

	@Deprecated("Use get(text: String? = null, " +
			"fLang: String? = null, " +
			"sLang: String? = null, " +
			"fCountry: String? = null, " +
			"sCountry: String? = null, " +
			"newest: Boolean = false, " +
			"position: Int? = null)")
	suspend fun get(text: String?, position: Int) = mr.get(
		text = text,
		position = position
	)?.toDTO()

	suspend fun get(
		text: String? = null,
		fLang: String? = null,
		sLang: String? = null,
		fCountry: String? = null,
		sCountry: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = mr.get(text, fLang, sLang, fCountry, sCountry, newest, position)?.toDTO()

	suspend fun getById(id: Int) = mr.getById(id)?.toDTO()

	suspend fun getView(
		text: String? = null,
		fLang: String? = null,
		sLang: String? = null,
		fCountry: String? = null,
		sCountry: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = mr.get(text, fLang, sLang, fCountry, sCountry, newest, position)?.toViewDTO()

	suspend fun getViewById(id: Int) = mr.getById(id)?.toViewDTO()

	fun getCountFlow() = mr.getCountFlow()

	fun getByIdFlow(id: Int) = mr.getByIdFlow(id).map { it?.toDTO() }

	suspend fun countGlobal(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null
	) = network.module.count(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond
	)

	suspend fun getGlobalView(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		number: Long
	) = network.module.getView(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond,
		number = number
	)

	suspend fun getGlobalView(id: UUID) = network.module.getView(id)

	fun isChanged(id: Int) = mr.isChanged(id)

	suspend fun share(id: Int): LocalModule? {
		val module = getById(id)
		return module?.let {
			val res = network.module.post(it.toGlobal())
			val updatedModule = it.update(res)
			update(updatedModule)
			return@let updatedModule
		}
	}

	suspend fun addToShare(mv: LocalModuleView) {
		if (getById(mv.id)?.changed != true) return
		val exists = dataProvider.share.exists(idModule = mv.id)
		if (exists) return
		else {
			dataProvider.share.save(LocalShare(idModule = mv.id))
			val size = dataProvider.moduleCard.getCountByModuleId(mv.id)
			for (i in 0 until size) dataProvider.moduleCard.let { mcp ->
				mcp.getView(mv.id, i)?.let { mcp.addToShare(it) }
			}
		}
	}

	suspend fun download(globalId: UUID): LocalModule? {
		val local = mr.getByGlobalId(globalId)
		val nm = network.module.get(globalId)
		val localId = if (local != null) {
			update(local.toDTO().update(nm))
			dataProvider.moduleCard.removeByModule(local.id)
			local.id
		} else {
			add(LocalModule().update(nm)).toInt()
		}
		return mr.getById(localId)?.toDTO()
	}

	suspend fun save(view: GlobalModuleView): LocalModule {
		val l1 = mr.getByGlobalId(view.globalId)
		if (l1 != null) mr.delete(l1)
		val localID = add(
			LocalModule(
				name = view.name,
				owner = view.user.globalOwner,
				timeChange = view.timeChange,
				like = view.like,
				dislike = view.dislike,
				globalId = view.globalId,
				globalOwner = view.user.globalOwner
			)
		).toInt()
		val l2 = mr.getById(localID)?.toDTO() ?: throw Exception("Module wasn't saved")
		dataProvider.moduleCard.save(l2)
		return l2
	}

}