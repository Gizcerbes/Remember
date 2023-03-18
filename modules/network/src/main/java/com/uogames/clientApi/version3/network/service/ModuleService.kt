package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.ModuleResponse
import com.uogames.clientApi.version3.network.response.ModuleViewResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class ModuleService(private val client: HttpClient) {

    suspend fun get(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ): ModuleResponse = client
        .get("/remember-card/v3/module") {
            text?.let { parameter("text", it) }
            langFirst?.let { parameter("lang-first", it) }
            langSecond?.let { parameter("lang-second", it) }
            countryFirst?.let { parameter("country-first", it) }
            countrySecond?.let { parameter("country-second", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): ModuleResponse = client
        .get("/remember-card/v3/module/$globalId")
        .ifSuccess()

    suspend fun getView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ): ModuleViewResponse = client
        .get("/remember-card/v3/module/view") {
            text?.let { parameter("text", it) }
            langFirst?.let { parameter("lang-first", it) }
            langSecond?.let { parameter("lang-second", it) }
            countryFirst?.let { parameter("country-first", it) }
            countrySecond?.let { parameter("country-second", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun getView(globalId: UUID): ModuleViewResponse = client
        .get("/remember-card/v3/module/view/$globalId")
        .ifSuccess()

    suspend fun count(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ): Long = client
        .get("/remember-card/v3/module/count") {
            text?.let { parameter("text", it) }
            langFirst?.let { parameter("lang-first", it) }
            langSecond?.let { parameter("lang-second", it) }
            countryFirst?.let { parameter("country-first", it) }
            countrySecond?.let { parameter("country-second", it) }
        }.ifSuccess()



    suspend fun post(module: ModuleResponse): ModuleResponse = client
        .post("/remember-card/v3/module") {
            contentType(ContentType.Application.Json)
            setBody(module)
        }.ifSuccess()

}