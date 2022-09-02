package com.uogames.repository.providers

import android.content.Context
import androidx.core.net.toUri
import com.uogames.database.DatabaseRepository
import com.uogames.database.repository.ImageRepository
import com.uogames.dto.local.Image
import com.uogames.dto.local.Card
import com.uogames.dto.local.Phrase
import com.uogames.map.ImageMap.update
import com.uogames.network.NetworkProvider
import com.uogames.network.provider.ImageProvider
import com.uogames.network.response.ImageResponse
import com.uogames.repository.DataProvider
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.flow.first
import java.util.*

class ImageProvider(
	private val dataProvider: DataProvider,
	private val database: ImageRepository,
	private val fileRepository: FileRepository,
	private val network: NetworkProvider
) {

	suspend fun add(bytes: ByteArray): Int {
		val id = database.insert(Image()).toInt()
		val uri = fileRepository.saveFile("$id.png", bytes)
		database.update(Image(id, uri.toString()))
		return id
	}

	suspend fun delete(image: Image): Boolean {
		return database.getByIdFlow(image.id).first()?.let {
			fileRepository.deleteFile(it.imgUri.toUri())
			database.delete(image)
		} ?: false
	}

	suspend fun update(image: Image, bytes: ByteArray): Boolean {
		return database.getByIdFlow(image.id).first()?.let {
			val uri = fileRepository.saveFile("${it.id}.png", bytes)
			return database.update(Image(image.id, uri.toString()))
		} ?: false
	}

	suspend fun readDataByImage(image: Image): ByteArray? {
		return fileRepository.readFile(image.imgUri.toUri())
	}

	suspend fun getById(id: Int) = database.getById(id)

	fun getByIdFlow(id: Int) = database.getByIdFlow(id)

	fun getByPhrase(phrase: Phrase) = database.getByPhraseFlow(phrase)

	fun getByCard(card: Card) = database.getByCardFlow(card)

	suspend fun getByGlobalId(id: UUID) = network.image.get(id)


	suspend fun clear() {
		database.freeImages().forEach {
			fileRepository.deleteFile(it.imgUri.toUri())
			database.delete(it)
		}
	}

	fun getListFlow() = database.getImageListFlow()

	suspend fun share(id: Int): Image? {
		val image = getById(id)
		image?.globalId?.let { if (network.image.exists(it)) return image }
		val res = image?.let {
			if (it.globalId == null) it
			else null
		}?.let {
			fileRepository.readFile(image.imgUri.toUri())?.let { network.image.upload(it) }
		}?.let { Image(image.id, image.imgUri, it.globalId, it.globalOwner) }
		res?.let { database.update(it) }
		return res ?: image
	}

	suspend fun download(id: UUID): Image? {
		val local = database.getByGlobalId(id)
		if (local == null) {
			val localId = add(network.image.load(id))
			val l = database.getById(localId) ?: return null
			l.update(network.image.get(id))
			database.update(l)
			return l
		}
		return local
	}

	fun getPicasso(context: Context) = network.getPicasso(context)

}