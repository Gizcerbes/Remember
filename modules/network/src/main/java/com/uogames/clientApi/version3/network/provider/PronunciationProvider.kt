package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.PronunciationMap.toDTO
import com.uogames.clientApi.version3.network.service.PronunciationService
import java.util.*

class PronunciationProvider(private val s: PronunciationService) {

    suspend fun get(globalId: UUID) = s.get(globalId).toDTO()

    suspend fun load(globalId: UUID) = s.load(globalId)

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ) = s.upload(
        byteArray = byteArray,
        onUpload = onUpload
    ).toDTO()

    suspend fun exists(globalId: UUID) = s.exists(globalId)


}