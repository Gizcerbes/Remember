package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.ifSuccess
import com.uogames.clientApi.version3.network.response.ModuleCardResponse
import com.uogames.clientApi.version3.network.response.ModuleCardViewResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

class ModuleCardService(private val client: HttpClient) {

    suspend fun count(moduleID: UUID): Long = client
        .get("/remember-card/v3/module-card/count/$moduleID")
        .ifSuccess()

    suspend fun get(
        moduleID: UUID? = null,
        number: Long
    ): ModuleCardResponse = client
        .get("/remember-card/v3/module-card") {
            moduleID?.let { parameter("module-id", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun get(globalId: UUID): ModuleCardResponse = client
        .get("/remember-card/v3/module-card/$globalId")
        .ifSuccess()

    suspend fun getView(
        moduleID: UUID? = null,
        number: Long
    ): ModuleCardViewResponse = client
        .get("/remember-card/v3/module-card/view") {
            moduleID?.let { parameter("module-id", it) }
            parameter("number", number)
        }.ifSuccess()

    suspend fun getView(globalId: UUID): ModuleCardViewResponse = client
        .get("/remember-card/v3/module-card/view/$globalId")
        .ifSuccess()

    suspend fun post(moduleCard: ModuleCardResponse): ModuleCardResponse = client
        .post("/remember-card/v3/module-card") {
            contentType(ContentType.Application.Json)
            setBody(moduleCard)
        }.ifSuccess()


}