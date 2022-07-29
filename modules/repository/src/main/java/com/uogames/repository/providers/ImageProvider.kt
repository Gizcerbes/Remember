package com.uogames.repository.providers

import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Image
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first

class ImageProvider(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) {

	suspend fun add(bytes: ByteArray): Int {
		val id = database.imageRepository.insert(Image()).toInt()
		val uri = fileRepository.saveFile("$id.png", bytes)
		database.imageRepository.update(Image(id, uri.toString()))
		return id
	}

	suspend fun delete(image: Image): Boolean {
		return database.imageRepository.getByIdFlow(image.id).first()?.let {
			fileRepository.deleteFile(it.imgUri.toUri())
			database.imageRepository.delete(image)
		} ?: false
	}

	suspend fun update(image: Image, bytes: ByteArray): Boolean {
		return database.imageRepository.getByIdFlow(image.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.png", bytes)
			return database.imageRepository.update(Image(image.id, uri.toString()))
		} ?: false
	}

	suspend fun readDataByImage(image: Image): ByteArray? {
		return fileRepository.readFile(image.imgUri.toUri())
	}

	suspend fun getById(id: Int) = database.imageRepository.getById(id)

	fun getByIdFlow(id: Int) = database.imageRepository.getByIdFlow(id)

	fun getByPhrase(phrase: Phrase) = database.imageRepository.getByPhraseFlow(phrase)

	fun getByCard(card: Card) = database.imageRepository.getByCardFlow(card)

	suspend fun clear() {
		database.imageRepository.freeImages().forEach {
			fileRepository.deleteFile(it.imgUri.toUri())
			database.imageRepository.delete(it)
		}
	}

	fun getListFlow() = database.imageRepository.getImageListFlow()

}