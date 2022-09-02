package com.uogames.database.repository

import com.uogames.database.dao.ImageDAO
import com.uogames.database.map.ImageMap.toDTO
import com.uogames.database.map.ImageMap.toEntity
import com.uogames.dto.local.Image
import com.uogames.dto.local.Card
import com.uogames.dto.local.Phrase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.*

class ImageRepository(private val imageDAO: ImageDAO) {

	suspend fun insert(image: Image) = imageDAO.insert(image.toEntity())

	suspend fun delete(image: Image) = imageDAO.delete(image.toEntity()) > 0

	suspend fun update(image: Image) = imageDAO.update(image.toEntity()) > 0

	suspend fun getById(id: Int) = imageDAO.getById(id)?.toDTO()

	suspend fun getByGlobalId(id: UUID) = imageDAO.getByGlobalId(id)?.toDTO()

	fun getByIdFlow(id: Int) = imageDAO.getByIdFlow(id).map { it?.toDTO() }

	fun getByPhraseFlow(phrase: Phrase) = phrase.idImage?.let { id ->
		imageDAO.getByIdFlow(id).map { it?.toDTO() }
	} ?: MutableStateFlow(null).asStateFlow()

	fun getByCardFlow(card: Card) = card.idImage?.let { id ->
		imageDAO.getByIdFlow(id).map { it?.toDTO() }
	} ?: MutableStateFlow(null).asStateFlow()

	suspend fun freeImages() = imageDAO.freeImages().map { it.toDTO() }

	fun getImageListFlow() = imageDAO.getListFlow().map { list -> list.map { it.toDTO() } }
}