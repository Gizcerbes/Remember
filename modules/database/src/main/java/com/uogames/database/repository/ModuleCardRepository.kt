package com.uogames.database.repository

import com.uogames.database.dao.ModuleCardDAO
import com.uogames.database.entity.ModuleCardEntity
import com.uogames.database.map.ModuleCardMap.toDTO
import com.uogames.database.map.ModuleCardMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalModuleCardView
import kotlinx.coroutines.flow.map
import java.util.*

class ModuleCardRepository(
    private val dao: ModuleCardDAO,
    private val map: ViewMap<ModuleCardEntity, LocalModuleCardView>
) {

    suspend fun insert(moduleCard: LocalModuleCard) = dao.insert(moduleCard.toEntity())

    suspend fun delete(moduleCard: LocalModuleCard) = dao.delete(moduleCard.toEntity()) > 0

    suspend fun update(moduleCard: LocalModuleCard) = dao.update(moduleCard.toEntity()) > 0

    fun getByModuleID(id: Int) = dao.getByModuleID(id).map { it.map { mc -> mc.toDTO() } }

    fun getByModule(module: LocalModule) = dao.getByModuleID(module.id).map { it.map { mc -> mc.toDTO() } }

    fun getCountByModuleIdFlow(id: Int) = dao.getCountByModuleIdFlow(id)

    suspend fun getCountByModuleId(id: Int) = dao.getCountByModuleId(id)

    suspend fun getByPositionOfModule(idModule: Int, position: Int) = dao.getByPositionOfModule(idModule, position)?.toDTO()

    suspend fun getViewByPositionOfModule(idModule: Int, position: Int) = dao.getByPositionOfModule(idModule, position)?.let { map.toDTO(it) }

    suspend fun getRandomModule() = dao.getRandomModuleCard()?.toDTO()

    suspend fun getRandomModule(idModule: Int) = dao.getRandomModuleCard(idModule)?.toDTO()

    suspend fun getRandomModuleView(idModule: Int) = dao.getRandomModuleCard(idModule)?.let { map.toDTO(it) }

    suspend fun getUnknowableView(idModule: Int) = dao.getUnknowable(idModule)?.let { map.toDTO(it) }

    suspend fun getConfusing(idModule: Int, idPhrase: Int) = dao.getConfusing(idModule, idPhrase)?.let { map.toDTO(it) }

    suspend fun getRandomModuleWithout(idCard: Int) = dao.getRandomWithout(idCard)?.toDTO()

    suspend fun getRandomModuleWithout(idModule: Int, idCard: Int) = dao.getRandomWithout(idModule, idCard)?.toDTO()

    suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

    suspend fun getViewById(id: Int) = dao.getById(id)?.let { map.toDTO(it) }

    suspend fun getByGlobalId(globalId: UUID) = dao.getByGlobalId(globalId)?.toDTO()

    suspend fun removeByModuleId(idModule: Int) = dao.removeByModule(idModule)

}