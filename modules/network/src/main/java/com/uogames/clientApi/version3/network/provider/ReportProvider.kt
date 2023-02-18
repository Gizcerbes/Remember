package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.ReportMap.toResponse
import com.uogames.clientApi.version3.network.service.ReportService
import com.uogames.dto.global.GlobalReport

class ReportProvider(private val s: ReportService) {

    suspend fun post(report: GlobalReport) = s.post(report.toResponse())


}