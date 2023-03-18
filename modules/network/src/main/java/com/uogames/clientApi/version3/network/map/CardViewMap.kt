package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.ImageViewMap.toDTO
import com.uogames.clientApi.version3.network.map.ImageViewMap.toResponse
import com.uogames.clientApi.version3.network.map.PhraseViewMap.toDTO
import com.uogames.clientApi.version3.network.map.PhraseViewMap.toResponse
import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.CardViewResponse
import com.uogames.dto.global.GlobalCardView

object CardViewMap: Map<CardViewResponse, GlobalCardView> {

    override fun CardViewResponse.toDTO() = GlobalCardView(
        globalId = globalId,
        user = user.toDTO(),
        phrase = phrase.toDTO(),
        translate = translate.toDTO(),
        image = image?.toDTO(),
        reason = reason,
        timeChange = timeChange,
        like = like,
        dislike = dislike
    )

    override fun GlobalCardView.toResponse() = CardViewResponse(
        globalId = globalId,
        user = user.toResponse(),
        phrase = phrase.toResponse(),
        translate = translate.toResponse(),
        image = image?.toResponse(),
        reason = reason,
        timeChange = timeChange,
        like = like,
        dislike = dislike
    )

}