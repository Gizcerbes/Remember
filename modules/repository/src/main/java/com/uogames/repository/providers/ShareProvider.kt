package com.uogames.repository.providers

import com.uogames.database.repository.ShareRepository
import com.uogames.dto.local.LocalShare
import com.uogames.repository.map.ShareMap.toDTO
import com.uogames.repository.map.ShareMap.toEntity

class ShareProvider(
    private val rep: ShareRepository
) {

    suspend fun save(share: LocalShare) = rep.save(share.toEntity())

    suspend fun remove(share: LocalShare) = rep.delete(share.toEntity())

    suspend fun count() = rep.count()

    fun countFlow() = rep.countFlow()

    suspend fun getFirst() = rep.getFirst()?.toDTO()

    suspend fun exists(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ) = rep.exists(id, idImage, idPronounce, idPhrase, idCard, idModule, idModuleCard)

    fun existsFlow(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ) = rep.existsFlow(id, idImage, idPronounce, idPhrase, idCard, idModule, idModuleCard)


    suspend fun clean() = rep.clean()

}