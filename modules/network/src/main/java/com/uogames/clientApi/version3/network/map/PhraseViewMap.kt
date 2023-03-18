package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.ImageViewMap.toDTO
import com.uogames.clientApi.version3.network.map.ImageViewMap.toResponse
import com.uogames.clientApi.version3.network.map.PronunciationViewMap.toDTO
import com.uogames.clientApi.version3.network.map.PronunciationViewMap.toResponse
import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.PhraseViewResponse
import com.uogames.dto.global.GlobalPhraseView

object PhraseViewMap: Map<PhraseViewResponse, GlobalPhraseView> {

    override fun PhraseViewResponse.toDTO() = GlobalPhraseView(
        globalId = globalId,
        user = globalOwner.toDTO(),
        phrase = phrase,
        definition = definition,
        lang = lang,
        country = country,
        pronounce = pronounce?.toDTO(),
        image = image?.toDTO(),
        timeChange = timeChange,
        like = like,
        dislike = dislike
    )

    override fun GlobalPhraseView.toResponse() = PhraseViewResponse(
        globalId = globalId,
        globalOwner = user.toResponse(),
        phrase = phrase,
        definition = definition,
        lang = lang,
        country = country,
        pronounce = pronounce?.toResponse(),
        image = image?.toResponse(),
        timeChange = timeChange,
        like = like,
        dislike = dislike
    )

}