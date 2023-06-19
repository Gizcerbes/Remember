package com.uogames.network.provider

import com.uogames.network.map.ImageMap.toDTO
import com.uogames.network.service.ImageService
import java.util.*

class ImageProvider(private val service: ImageService) {

	suspend fun get(globalId: UUID) = service.get(globalId).toDTO()

	suspend fun load(globalId: UUID) = service.load(globalId)

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	) = service.upload(byteArray, onUpload).toDTO()

	suspend fun exists(globalId: UUID) = service.exists(globalId)
}