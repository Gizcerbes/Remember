package com.uogames.database.repository

import com.uogames.database.dao.ModuleDAO
import com.uogames.database.entity.ModuleEntity
import com.uogames.database.map.ModuleMap.toDTO
import com.uogames.database.map.ModuleMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleView
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleRepository(
    private val dao: ModuleDAO,
    private val map: ViewMap<ModuleEntity, LocalModuleView>
) {

    suspend fun insert(module: LocalModule) = dao.insert(module.toEntity())

    suspend fun delete(module: LocalModule) = dao.delete(module.toEntity()) > 0

    suspend fun update(module: LocalModule) = dao.update(module.toEntity()) > 0

    suspend fun count(text: String?): Int {
        return if (text == null) dao.count()
        else dao.count(text)
    }

    suspend fun get(text: String?, position: Int): LocalModule? {
        return if (text == null) dao.get(position)?.toDTO()
        else dao.get(text, position)?.toDTO()
    }

    suspend fun getView(text: String?, position: Int) : LocalModuleView? {
        return if (text == null) dao.get(position)?.let { map.toDTO(it) }
        else dao.get(text, position)?.let { map.toDTO(it) }
    }

    fun getCount() = dao.getCount()

    fun getCountLike(like: String) = dao.getCountLike(like)

    fun getList() = dao.getList().map { it.map { module -> module.toDTO() } }

    fun getListLike(like: String) = dao.getListLike(like).map { it.map { module -> module.toDTO() } }

    suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

    suspend fun getViewById(id: Int) = dao.getById(id)?.let { map.toDTO(it) }

    suspend fun getByGlobalId(globalId: UUID) = dao.getByGlobalId(globalId)?.toDTO()

    suspend fun getByPosition(like: String, position: Int) = dao.getByPosition(like, position)?.toDTO()

    fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

}