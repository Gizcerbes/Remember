package com.uogames.clientApi.version3.network.service

import com.google.gson.Gson
import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.PronunciationResponse
import com.uogames.clientApi.version3.network.response.PronunciationViewResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import java.util.*

class PronunciationService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): PronunciationResponse = client
        .get("/remember-card/v3/pronunciation/info/$globalId")
        .ifSuccess()

    suspend fun getView(globalId: UUID): PronunciationViewResponse = client
        .get("/remember-card/v3/pronunciation/info/view/$globalId")
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

    suspend fun uploadV2(
        byteArray: ByteArray,
        pronounceResponse: PronunciationResponse,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): PronunciationResponse = client.post("/remember-card/v3/pronunciation/upload/v2"){
        contentType(ContentType.MultiPart.FormData)
        setBody(MultiPartFormDataContent(formData {
            append("json", Gson().toJson(pronounceResponse), Headers.build {
                append(HttpHeaders.ContentType, "application/json")
            })
            append("audio", byteArray, Headers.build {
                append(HttpHeaders.ContentType, "audio/mp4")
                append(HttpHeaders.ContentDisposition, "filename=\"${pronounceResponse.globalId}.mp4\"")
            })
        }))
        onUpload(onUpload)
    }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client
        .head("/remember-card/v3/pronunciation/info/$globalId")
        .status == HttpStatusCode.OK
}