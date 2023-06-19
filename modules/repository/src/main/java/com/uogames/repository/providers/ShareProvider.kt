package com.uogames.repository.providers

import com.uogames.database.repository.ShareRepository
import com.uogames.dto.local.LocalShare

class ShareProvider(
    private val rep: ShareRepository
) {

    suspend fun save(share: LocalShare) = rep.save(share)

    suspend fun remove(share: LocalShare) = rep.delete(share)

    suspend fun count() = rep.count()

    fun countFlow() = rep.countFlow()

    suspend fun getFirst() = rep.getFirst()

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