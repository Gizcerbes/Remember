package com.uogames.network.service

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient
import com.uogames.network.ifSuccess
import com.uogames.network.response.ImageResponse
import java.util.*

class ImageService(private val client: HttpClient) {

	suspend fun get(globalId: UUID): ImageResponse = client.get("/image/info/$globalId").ifSuccess()

	suspend fun load(globalId: UUID): ByteArray = client.get("/image/load/$globalId").ifSuccess()

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	): ImageResponse = client.post("/image/upload") {
		contentType(ContentType.Image.PNG)
		setBody(byteArray)
		onUpload(onUpload)
	}.ifSuccess()

	suspend fun exists(globalId: UUID): Boolean = client.head("/image/info/$globalId").status == HttpStatusCode.OK

}