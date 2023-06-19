package com.uogames.repository.providers

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.clientApi.version3.network.response.ImageResponse
import com.uogames.database.repository.ImageRepository
import com.uogames.dto.global.GlobalImageView
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalImageView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalShare
import com.uogames.map.ImageMap.update
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
        val id = database.insert(LocalImage()).toInt()
        val uri = fileRepository.saveFile("$id.png", bytes)
        database.update(LocalImage(id, uri.toString()))
        return id
    }

    suspend fun delete(image: LocalImage): Boolean {
        return database.getByIdFlow(image.id).first()?.let {
            fileRepository.deleteFile(it.imgUri.toUri())
            database.delete(image)
        } ?: false
    }

    suspend fun update(image: LocalImage, bytes: ByteArray): Boolean {
        return database.getByIdFlow(image.id).first()?.let {
            val uri = fileRepository.saveFile("${it.id}.png", bytes)
            return database.update(LocalImage(image.id, uri.toString()))
        } ?: false
    }

    suspend fun getById(id: Int) = database.getById(id)
    fun getByIdFlow(id: Int) = database.getByIdFlow(id)
    fun getByPhrase(phrase: LocalPhrase) = database.getByPhraseFlow(phrase)

    suspend fun getView(id: Int) = database.getViewByID(id)
    suspend fun getByGlobalId(id: UUID) = database.getByGlobalId(id)
    suspend fun getGlobalById(id: UUID) = network.image.get(id)
    suspend fun getGlobalView(id: UUID) = network.image.getView(id)

    fun load(image: LocalImageView): ByteArray? = fileRepository.readFile(image.imgUri.toUri())

    suspend fun clear() {
        database.freeImages().forEach {
            fileRepository.deleteFile(it.imgUri.toUri())
            database.delete(it)
        }
    }

    fun getListFlow() = database.getImageListFlow()

    suspend fun share(id: Int): LocalImage? {
        val image = getById(id)
        image?.globalId?.let {
            if (network.image.exists(it)) return image
        }
        val res = image?.let {
            fileRepository.readFile(image.imgUri.toUri())?.let { network.image.upload(it) }
        }?.let { LocalImage(image.id, image.imgUri, it.globalId, it.globalOwner) }
        res?.let { database.update(it) }
        return res ?: image
    }

    suspend fun shareV2(id: Int): LocalImage? {
        val image = getById(id)
        image?.globalId?.let {
            if (network.image.exists(it)) return image
        }

        val res = image?.let {
            fileRepository.readFile(image.imgUri.toUri())?.let {
                network.image.uploadV2(
                    byteArray = it,
                    ImageResponse(image.globalId, imageUri = "")
                )
            }
        }?.let { LocalImage(image.id, image.imgUri, it.globalId, it.globalOwner) }
        res?.let { database.update(it) }
        return res ?: image
    }

    suspend fun addToShare(iv: LocalImageView) {
            val r = dataProvider.share.exists(idImage = iv.id)
            if (!r) dataProvider.share.save(LocalShare(idImage = iv.id))
    }

    suspend fun download(id: UUID): LocalImage? {
        val local = database.getByGlobalId(id)
        if (local == null) {
            val localId = add(network.image.load(id))
            val l = database.getById(localId)?.update(network.image.get(id)) ?: return null
            database.update(l)
            return l
        }
        return local
    }

    suspend fun save(view: GlobalImageView): LocalImage {
        val l1 = database.getByGlobalId(view.globalId)
        return if (l1 == null) {
            val localID = add(network.image.load(view.globalId))
            val l = database.getById(localID)?.update(view) ?: throw Exception("Image wasn't saved")
            database.update(l)
            l
        } else {
            l1
        }
    }

    fun getPicasso(context: Context) = network.getPicasso(context)

}