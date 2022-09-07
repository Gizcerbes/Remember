package com.uogames.network.service

import io.ktor.client.call.*
import com.uogames.network.response.PronunciationResponse
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import com.uogames.network.HttpClient
import com.uogames.network.ifSuccess
import java.util.*

class PronunciationService(private val client: HttpClient) {

	suspend fun get(globalId: UUID): PronunciationResponse = client.get("/pronunciation/info/$globalId").ifSuccess()

	suspend fun load(globalId: UUID): ByteArray = client.get("/pronunciation/load/$globalId").ifSuccess()

	suspend fun upload(
		byteArray: ByteArray,
		onUpload: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> }
	): PronunciationResponse = client.post("/pronunciation/upload"){
		contentType(ContentType.Audio.MP4)
		setBody(byteArray)
		onUpload(onUpload)
	}.ifSuccess()

	suspend fun exists(globalId: UUID):Boolean = client.head("/pronunciation/info/$globalId").status == HttpStatusCode.OK
}