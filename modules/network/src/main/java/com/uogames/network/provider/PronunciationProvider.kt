package com.uogames.network.provider

import com.uogames.network.map.PronunciationMap.toDTO
import com.uogames.network.service.PronunciationService
import java.util.*

class PronunciationProvider(private val service: PronunciationService) {

	suspend fun get(globalId: UUID) = service.get(globalId).toDTO()

	suspend fun load(globalId: UUID) = service.load(globalId)

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	) = service.upload(byteArray, onUpload).toDTO()

	suspend fun exists(globalId: UUID):Boolean = service.exists(globalId)
}