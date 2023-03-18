package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.PronunciationViewResponse
import com.uogames.dto.global.GlobalPronunciationView

object PronunciationViewMap: Map<PronunciationViewResponse, GlobalPronunciationView> {

    override fun PronunciationViewResponse.toDTO() = GlobalPronunciationView(
        globalId = globalId,
        user = user.toDTO(),
        audioUri = audioUri
    )

    override fun GlobalPronunciationView.toResponse() = PronunciationViewResponse(
        globalId = globalId,
        user = user.toResponse(),
        audioUri = audioUri
    )

}