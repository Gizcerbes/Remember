package com.uogames.clientApi.version3.network.service

import android.util.Log
import com.google.gson.Gson
import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.ImageResponse
import com.uogames.clientApi.version3.network.response.ImageViewResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.content.PartData
import java.util.*

class ImageService(private val client: HttpClient) {

    suspend fun get(globalId: UUID): ImageResponse = client.get("/remember-card/v3/image/info/$globalId").ifSuccess()

    suspend fun getView(globalId: UUID): ImageViewResponse = client.get("/remember-card/v3/image/info/view/$globalId").ifSuccess()

    suspend fun load(globalId: UUID): ByteArray = client.get("/remember-card/v3/image/load/$globalId").ifSuccess()

    suspend fun upload(
        byteArray: ByteArray, onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): ImageResponse = client.post("/remember-card/v3/image/upload") {
        contentType(ContentType.Image.PNG)
        setBody(byteArray)
        onUpload(onUpload)
    }.ifSuccess()

    suspend fun uploadV2(
        byteArray: ByteArray,
        imageResponse: ImageResponse,
        onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
    ): ImageResponse = client.post("/remember-card/v3/image/upload/v2") {
        contentType(ContentType.MultiPart.FormData)
        setBody(MultiPartFormDataContent(formData {
            append("json", Gson().toJson(imageResponse), Headers.build {
                append(HttpHeaders.ContentType, "application/json")
            })
            append("image", byteArray, Headers.build {
                append(HttpHeaders.ContentType, "image/png")
                append(HttpHeaders.ContentDisposition, "filename=\"${imageResponse.globalId}.png\"")
            })
        }))
        onUpload(onUpload)
    }.ifSuccess()

    suspend fun exists(globalId: UUID): Boolean = client.head("/remember-card/v3/image/info/$globalId").status == HttpStatusCode.OK

}