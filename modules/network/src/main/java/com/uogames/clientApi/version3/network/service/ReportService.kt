package com.uogames.clientApi.version3.network.service

import com.uogames.clientApi.version3.network.response.ReportResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class ReportService(private val client: HttpClient) {

    suspend fun post(report: ReportResponse): Boolean = client
        .post("/remember-card/v3/report"){
            contentType(ContentType.Application.Json)
            setBody(report)
        }.status == HttpStatusCode.OK



}