package com.uogames.repository

import com.uogames.database.DatabaseRepository
import com.uogames.dto.Image
import com.uogames.dto.NewCard
import com.uogames.dto.Phrase
import kotlinx.coroutines.async

class ImageProvider(
	private val database: DatabaseRepository
) : Provider() {

	fun addAsync(image: Image) = ioScope.async { database.imageRepository.insert(image) }

	fun deleteAsync(image: Image) = ioScope.async { database.imageRepository.delete(image) }

	fun updateAsync(image: Image) = ioScope.async { database.imageRepository.update(image) }

	fun getById(id: Int) = database.imageRepository.getByID(id)

	fun getByPhrase(phrase: Phrase) = database.imageRepository.getByPhrase(phrase)

	fun getByCard(card: NewCard) = database.imageRepository.getByCard(card)

	fun cleanAsync() = ioScope.async { database.imageRepository.clean() }
}