package com.uogames.database.repository

import com.uogames.database.dao.ImageDAO
import com.uogames.database.entity.ImageEntity
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class ImageRepository(
    private val imageDAO: ImageDAO,
) {
    suspend fun insert(image: ImageEntity) = imageDAO.insert(image)

    suspend fun delete(image: ImageEntity) = imageDAO.delete(image) > 0

    suspend fun update(image: ImageEntity) = imageDAO.update(image) > 0

    suspend fun getById(id: Int) = imageDAO.getById(id)

    suspend fun getByGlobalId(id: String) = imageDAO.getByGlobalId(id)

    fun getByIdFlow(id: Int) = imageDAO.getByIdFlow(id)

    fun getByPhraseFlow(phrase: PhraseEntity) = phrase.idImage?.let { id ->
        imageDAO.getByIdFlow(id)
    } ?: MutableStateFlow(null).asStateFlow()

    suspend fun freeImages() = imageDAO.freeImages()

    fun getImageListFlow() = imageDAO.getListFlow()
}