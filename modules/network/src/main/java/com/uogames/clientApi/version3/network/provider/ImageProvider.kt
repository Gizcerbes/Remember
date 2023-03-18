package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.ImageMap.toDTO
import com.uogames.clientApi.version3.network.map.ImageViewMap.toDTO
import com.uogames.clientApi.version3.network.service.ImageService
import java.util.*

class ImageProvider(private val s: ImageService) {

    suspend fun get(globalId: UUID) = s.get(globalId).toDTO()

    suspend fun getView(globalId: UUID) = s.getView(globalId).toDTO()

    suspend fun load(globalId: UUID) = s.load(globalId)

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ) = s.upload(byteArray, onUpload).toDTO()

    suspend fun exists(globalId: UUID) = s.exists(globalId)


}