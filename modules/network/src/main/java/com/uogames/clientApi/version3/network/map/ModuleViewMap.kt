package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.ModuleViewResponse
import com.uogames.dto.global.GlobalModuleView

object ModuleViewMap: Map<ModuleViewResponse, GlobalModuleView> {

    override fun ModuleViewResponse.toDTO() = GlobalModuleView(
        globalId = globalId,
        user = user.toDTO(),
        timeChange = timeChange,
        name = name,
        like = like,
        dislike = dislike
    )

    override fun GlobalModuleView.toResponse() = ModuleViewResponse(
        globalId = globalId,
        user = user.toResponse(),
        timeChange = timeChange,
        name = name,
        like = like,
        dislike = dislike
    )

}