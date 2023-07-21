package com.uogames.repository.providers

import android.content.Context
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
import com.uogames.repository.map.ImageMap.toDTO
import com.uogames.repository.map.ImageMap.toEntity
import com.uogames.repository.map.ImageMap.toViewDTO
import com.uogames.repository.map.PhraseViewMap.toEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class ImageProvider(
    private val dataProvider: DataProvider,
    private val database: ImageRepository,
    private val fileRepository: FileRepository,
    private val network: NetworkProvider
) {

    suspend fun add(bytes: ByteArray): Int {
        val id = database.insert(LocalImage().toEntity()).toInt()
        val uri = fileRepository.saveFile("$id.png", bytes)
        database.update(LocalImage(id, uri.toString()).toEntity())
        return id
    }

    suspend fun delete(image: LocalImage): Boolean {
        return database.getByIdFlow(image.id).first()?.let {
            fileRepository.deleteFile(it.imgUri.toUri())
            database.delete(image.toEntity())
        } ?: false
    }

    suspend fun update(image: LocalImage, bytes: ByteArray): Boolean {
        return database.getByIdFlow(image.id).first()?.let {
            val uri = fileRepository.saveFile("${it.id}.png", bytes)
            return database.update(LocalImage(image.id, uri.toString()).toEntity())
        } ?: false
    }

    suspend fun getById(id: Int) = database.getById(id)?.toDTO()

    fun getByIdFlow(id: Int) = database.getByIdFlow(id).map { it?.toDTO() }

    fun getByPhrase(phrase: LocalPhrase) = database.getByPhraseFlow(phrase.toEntity()).map { it?.toDTO() }

    suspend fun getViewById(id: Int) = database.getById(id)?.toViewDTO()

    suspend fun getByGlobalId(id: UUID) = database.getByGlobalId(id.toString())?.toDTO()

    fun load(image: LocalImageView): ByteArray? = fileRepository.readFile(image.imgUri.toUri())

    suspend fun clear() {
        database.freeImages().forEach {
            fileRepository.deleteFile(it.imgUri.toUri())
            database.delete(it)
        }
    }

    fun getListFlow() = database.getImageListFlow().map { list -> list.map { it.toDTO() } }

    suspend fun share(id: Int): LocalImage? {
        val image = getById(id)
        image?.globalId?.let {
            if (network.image.exists(it)) return image
        }
        val res = image?.let {
            fileRepository.readFile(image.imgUri.toUri())?.let { network.image.upload(it) }
        }?.let { LocalImage(image.id, image.imgUri, it.globalId, it.globalOwner) }
        res?.let { database.update(it.toEntity()) }
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
        res?.let { database.update(it.toEntity()) }
        return res ?: image
    }

    suspend fun addToShare(iv: LocalImageView) {
            val r = dataProvider.share.exists(idImage = iv.id)
            if (!r) dataProvider.share.save(LocalShare(idImage = iv.id))
    }

    suspend fun download(id: UUID): LocalImage? {
        val local = database.getByGlobalId(id.toString())
        if (local == null) {
            val localId = add(network.image.load(id))
            val l = database.getById(localId)?.toDTO()?.update(network.image.get(id)) ?: return null
            database.update(l.toEntity())
            return l
        }
        return local.toDTO()
    }

    suspend fun save(view: GlobalImageView): LocalImage {
        val l1 = database.getByGlobalId(view.globalId.toString())
        return if (l1 == null) {
            val localID = add(network.image.load(view.globalId))
            val l = database.getById(localID)?.toDTO()?.update(view) ?: throw Exception("Image wasn't saved")
            database.update(l.toEntity())
            l
        } else {
            l1.toDTO()
        }
    }

    suspend fun fastSave(view: GlobalImageView): Int {
        val l1 = database.getByGlobalId(view.globalId.toString())
        return l1?.id ?: add(network.image.load(view.globalId))
    }

    fun getPicasso(context: Context) = network.getPicasso(context)

}