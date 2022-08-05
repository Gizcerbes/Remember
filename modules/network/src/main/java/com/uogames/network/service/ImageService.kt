package com.uogames.network.service

import com.uogames.network.response.ImageResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class ImageService(private val client: HttpClient) {

	suspend fun get(globalId: Long): ImageResponse = client.get("/image/$globalId").body()

	suspend fun load(globalId: Long): ByteArray = client.get("/image/load/$globalId").body()

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	): ImageResponse = client.post("/image/upload"){
		setBody(MultiPartFormDataContent(
			formData {
				append("description", "Image")
				append("image", byteArray, Headers.build {
					append(HttpHeaders.ContentType, "image/png")
				})
			}
		))
		onUpload(onUpload)
	}.body()
}