package com.uogames.network.provider

import com.uogames.network.map.ImageMap.toDTO
import com.uogames.network.service.ImageService

class ImageProvider(private val service: ImageService) {

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun load(globalId: Long) = service.load(globalId)

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	) = service.upload(byteArray, onUpload).toDTO()
}