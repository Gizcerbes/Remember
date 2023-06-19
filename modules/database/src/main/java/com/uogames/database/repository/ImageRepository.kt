package com.uogames.database.repository

import com.uogames.database.dao.ImageDAO
import com.uogames.database.entity.ImageEntity
import com.uogames.database.map.ImageMap.toDTO
import com.uogames.database.map.ImageMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhrase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.*

class ImageRepository(
    private val imageDAO: ImageDAO,
    private val map: ViewMap<ImageEntity, LocalImageView>
) {
    suspend fun insert(image: LocalImage) = imageDAO.insert(image.toEntity())

    suspend fun delete(image: LocalImage) = imageDAO.delete(image.toEntity()) > 0

    suspend fun update(image: LocalImage) = imageDAO.update(image.toEntity()) > 0

    suspend fun getById(id: Int) = imageDAO.getById(id)?.toDTO()

    suspend fun getViewByID(id: Int) = imageDAO.getById(id)
        ?.let { map.toDTO(it) }

    suspend fun getByGlobalId(id: UUID) = imageDAO.getByGlobalId(id)?.toDTO()

    fun getByIdFlow(id: Int) = imageDAO.getByIdFlow(id).map { it?.toDTO() }

    fun getByPhraseFlow(phrase: LocalPhrase) = phrase.idImage?.let { id ->
        imageDAO.getByIdFlow(id).map { it?.toDTO() }
    } ?: MutableStateFlow(null).asStateFlow()

    fun getByCardFlow(card: LocalCard) = card.idImage?.let { id ->
        imageDAO.getByIdFlow(id).map { it?.toDTO() }
    } ?: MutableStateFlow(null).asStateFlow()

    suspend fun freeImages() = imageDAO.freeImages().map { it.toDTO() }

    fun getImageListFlow() = imageDAO.getListFlow().map { list -> list.map { it.toDTO() } }
}