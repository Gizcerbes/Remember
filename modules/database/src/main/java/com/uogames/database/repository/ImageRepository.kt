package com.uogames.database.repository

import com.uogames.database.dao.ImageDAO
import com.uogames.database.map.ImageMap.toDTO
import com.uogames.database.map.ImageMap.toEntity
import com.uogames.dto.Image
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ImageRepository(private val imageDAO: ImageDAO) {

	suspend fun insert(image: Image) = imageDAO.insert(image.toEntity())

	suspend fun delete(image: Image) = imageDAO.delete(image.toEntity()) > 0

	suspend fun update(image: Image) = imageDAO.update(image.toEntity()) > 0

	fun getByID(id: Int) = imageDAO.getByID(id).map { it?.toDTO() }

	fun getByPhrase(phrase: Phrase) = phrase.idImage?.let { id ->
		imageDAO.getByID(id).map { it?.toDTO() }
	} ?: MutableStateFlow(null).asStateFlow()

	fun getByCard(card: Card) = card.idImgBase64?.let { id ->
		imageDAO.getByID(id).map { it?.toDTO() }
	} ?: MutableStateFlow(Image(0, "")).asStateFlow()

	suspend fun freeImages() = imageDAO.freeImages().map { it.toDTO() }

}