package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.ImageResponse
import com.uogames.clientApi.version3.network.response.ImageViewResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.*
import java.util.*

class ImageService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): ImageResponse = client
        .get("/remember-card/v3/image/info/$globalId")
        .ifSuccess()

    suspend fun getView(globalId: UUID): ImageViewResponse = client
        .get("/remember-card/v3/image/info/view/$globalId")
        .ifSuccess()

    suspend fun load(globalId: UUID): ByteArray = client
        .get("/remember-card/v3/image/load/$globalId")
        .ifSuccess()

    suspend fun upload(
        byteArray: ByteArray,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): ImageResponse = client
        .post("/remember-card/v3/image/upload") {
            contentType(ContentType.Image.PNG)
            setBody(byteArray)
            onUpload(onUpload)
        }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client
        .head("/remember-card/v3/image/info/$globalId")
        .status == HttpStatusCode.OK

}