package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.PronunciationResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.*
import java.util.*

class PronunciationService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): PronunciationResponse = client
        .get("/remember-card/v3/pronunciation/info/$globalId")
        .ifSuccess()

    suspend fun load(globalId: UUID): ByteArray = client
        .get("/remember-card/v3/pronunciation/load/$globalId")
        .ifSuccess()

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): PronunciationResponse = client
        .post("/remember-card/v3/pronunciation/upload") {
            contentType(ContentType.Audio.MP4)
            setBody(byteArray)
            onUpload(onUpload)
        }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client
        .head("/remember-card/v3/pronunciation/info/$globalId")
        .status == HttpStatusCode.OK
}