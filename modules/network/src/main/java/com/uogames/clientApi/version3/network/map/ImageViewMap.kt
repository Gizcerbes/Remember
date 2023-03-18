package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.ImageViewResponse
import com.uogames.dto.global.GlobalImageView

object ImageViewMap: Map<ImageViewResponse, GlobalImageView> {

    override fun ImageViewResponse.toDTO() = GlobalImageView(
        globalId = globalId,
        user = user.toDTO(),
        imageUri = imageUri
    )

    override fun GlobalImageView.toResponse() = ImageViewResponse(
        globalId = globalId,
        user = user.toResponse(),
        imageUri = imageUri
    )

}