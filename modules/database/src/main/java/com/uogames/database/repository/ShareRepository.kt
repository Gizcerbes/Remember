package com.uogames.database.repository

import com.uogames.database.dao.ShareDAO
import com.uogames.database.map.ShareMapper.toDTO
import com.uogames.database.map.ShareMapper.toEntity
import com.uogames.dto.local.LocalShare

class ShareRepository(
    private val dao: ShareDAO
) {

    suspend fun save(share: LocalShare) = dao.insert(share.toEntity())

    suspend fun delete(share: LocalShare) = dao.delete(share.toEntity())

    suspend fun count() = dao.count()

    fun countFlow() = dao.countFlow()

    suspend fun getFirst() = dao.getFirst()?.toDTO()

    suspend fun exists(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ) = dao.exists(
        id = id,
        idImage = idImage,
        idPronounce = idPronounce,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule,
        idModuleCard = idModuleCard
    )

    fun existsFlow(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ) = dao.existsFlow(
        id = id,
        idImage = idImage,
        idPronounce = idPronounce,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule,
        idModuleCard = idModuleCard
    )

    suspend fun clean() = dao.clean()

}