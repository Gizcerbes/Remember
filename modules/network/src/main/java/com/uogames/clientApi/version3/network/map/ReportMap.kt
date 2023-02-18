package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.ReportResponse
import com.uogames.dto.global.GlobalReport

object ReportMap : Map<ReportResponse, GlobalReport> {

    override fun ReportResponse.toDTO() = GlobalReport(
        globalId = globalId,
        claimant = claimant,
        message = message,
        accused = accused,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule
    )

    override fun GlobalReport.toResponse() = ReportResponse(
        globalId = globalId,
        claimant = claimant,
        message = message,
        accused = accused,
        idPhrase = idPhrase,
        idCard = idCard,
        idModule = idModule
    )

}