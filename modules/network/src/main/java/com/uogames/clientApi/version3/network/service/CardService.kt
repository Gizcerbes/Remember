package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.CardResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class CardService(private val client: HttpClient) {

    suspend fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ): CardResponse = client.get("/remember-card/v3/card") {
        text?.let { parameter("text", it) }
        langFirst?.let { parameter("lang-first", it) }
        langSecond?.let { parameter("lang-second", it) }
        countryFirst?.let { parameter("country-first", it) }
        countrySecond?.let { parameter("country-second", it) }
        parameter("number", number)
    }.ifSuccess()

    suspend fun get(globalId: UUID): CardResponse = client
        .get("/remember-card/v3/card/$globalId")
        .ifSuccess()

    suspend fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ): Long = client.get("/remember-card/v3/card/count") {
        text?.let { parameter("text", it) }
        langFirst?.let { parameter("lang-first", it) }
        langSecond?.let { parameter("lang-second", it) }
        countryFirst?.let { parameter("country-first", it) }
        countrySecond?.let { parameter("country-second", it) }
    }.ifSuccess()

    suspend fun post(card: CardResponse): CardResponse = client.post("/remember-card/v3/card") {
        contentType(ContentType.Application.Json)
        setBody(card)
    }.ifSuccess()

}