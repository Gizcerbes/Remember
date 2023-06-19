package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.PhraseResponse
import com.uogames.clientApi.version3.network.response.PhraseViewResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class PhraseService(private val client: HttpClient) {

    suspend fun count(
        text: String? = null,
        lang: String? = null,
        country: String? = null
    ): Long = client
        .get("/remember-card/v3/phrase/count") {
            text?.let { parameter("text", it) }
            lang?.let { parameter("lang", it) }
            country?.let { parameter("country", it) }
        }.ifSuccess()

    suspend fun get(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long
    ): PhraseResponse = client
        .get("/remember-card/v3/phrase") {
            text?.let { parameter("text", it) }
            lang?.let { parameter("lang", it) }
            country?.let { parameter("country", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): PhraseResponse = client
        .get("/remember-card/v3/phrase/$globalId")
        .ifSuccess()

    suspend fun getView(
        text: String? = null,
        lang: String? = null,
        country: String? = null,
        number: Long
    ): PhraseViewResponse = client
        .get("/remember-card/v3/phrase/view") {
            text?.let { parameter("text", it) }
            lang?.let { parameter("lang", it) }
            country?.let { parameter("country", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun getView(globalId: UUID): PhraseViewResponse = client
        .get("/remember-card/v3/phrase/view/$globalId")
        .ifSuccess()

    suspend fun post(phrase: PhraseResponse): PhraseResponse = client
        .post("/remember-card/v3/phrase") {
            contentType(ContentType.Application.Json)
            setBody(phrase)
        }.ifSuccess()

}