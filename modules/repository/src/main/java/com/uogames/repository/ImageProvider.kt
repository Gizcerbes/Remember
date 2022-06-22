package com.uogames.repository

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
) : Provider() {

	fun addAsync(image: Image, bytes: ByteArray) = ioScope.async {
		val id = database.imageRepository.insert(image).toInt()
		val uri = fileRepository.saveFile("$id.png", bytes)
		database.imageRepository.update(Image(id,uri.toString()))
		return@async id
	}

	fun deleteAsync(image: Image) = ioScope.async {
		database.imageRepository.getByID(image.id).first()?.let {
			fileRepository.deleteFile(it.imgBase64.toUri())
			return@async database.imageRepository.delete(image)
		} ?: false
	}

	fun updateAsync(image: Image, bytes: ByteArray) = ioScope.async {
		database.imageRepository.getByID(image.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.png", bytes)
			return@async database.imageRepository.update(Image(image.id, uri.toString()))
		} ?: false
	}

	fun getById(id: Int) = database.imageRepository.getByID(id)

	fun getByPhrase(phrase: Phrase) = database.imageRepository.getByPhrase(phrase)

	fun getByCard(card: Card) = database.imageRepository.getByCard(card)

	suspend fun clear(){
		database.imageRepository.freeImages().forEach{
			fileRepository.deleteFile(it.imgBase64.toUri())
			database.imageRepository.delete(it)
		}
	}

}