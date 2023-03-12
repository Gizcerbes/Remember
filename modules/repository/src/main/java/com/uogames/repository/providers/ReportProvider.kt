package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.dto.global.GlobalReport

class ReportProvider(
    private val network: NetworkProvider
) {

    suspend fun send(report: GlobalReport) = network.report.post(report)


}