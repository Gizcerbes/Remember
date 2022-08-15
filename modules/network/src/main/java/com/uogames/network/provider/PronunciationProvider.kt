package com.uogames.network.provider

import com.uogames.network.map.PronunciationMap.toDTO
import com.uogames.network.service.PronunciationService

class PronunciationProvider(private val service: PronunciationService) {

	suspend fun get(globalId: Long) = service.get(globalId).toDTO()

	suspend fun load(globalId: Long) = service.load(globalId)

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	) = service.upload(byteArray, onUpload).toDTO()

}