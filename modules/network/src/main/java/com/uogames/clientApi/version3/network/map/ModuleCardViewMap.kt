package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.map.CardViewMap.toDTO
import com.uogames.clientApi.version3.network.map.CardViewMap.toResponse
import com.uogames.clientApi.version3.network.map.ModuleViewMap.toDTO
import com.uogames.clientApi.version3.network.map.ModuleViewMap.toResponse
import com.uogames.clientApi.version3.network.map.UserViewMap.toDTO
import com.uogames.clientApi.version3.network.map.UserViewMap.toResponse
import com.uogames.clientApi.version3.network.response.ModuleCardViewResponse
import com.uogames.dto.global.GlobalModuleCardView

object ModuleCardViewMap: Map<ModuleCardViewResponse, GlobalModuleCardView> {

    override fun ModuleCardViewResponse.toDTO() = GlobalModuleCardView(
        globalId = globalId,
        user = user.toDTO(),
        module = module.toDTO(),
        card = idCard.toDTO()
    )

    override fun GlobalModuleCardView.toResponse() = ModuleCardViewResponse(
        globalId = globalId,
        user = user.toResponse(),
        module = module.toResponse(),
        idCard = card.toResponse()
    )

}