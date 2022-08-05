package com.uogames.network.service

import com.uogames.network.response.PronunciationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class PronunciationService(private val client: HttpClient) {

	suspend fun get(globalId: Long): PronunciationResponse = client.get("/pronunciation/$globalId").body()

	suspend fun load(globalId: Long): ByteArray = client.get("/pronunciation/load/$globalId").body()

	suspend fun upload(
		byteData: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	): PronunciationResponse = client.post("/pronunciation/upload") {
		setBody(MultiPartFormDataContent(
			formData {
				append("description", "Pronunciation")
				append("audio", byteData, Headers.build {
					append(HttpHeaders.ContentType, "audio/3gpp")
				})
			}
		))
		onUpload(onUpload)
	}.body()

}