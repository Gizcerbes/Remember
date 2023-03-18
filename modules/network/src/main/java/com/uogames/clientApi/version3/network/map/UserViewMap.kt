package com.uogames.clientApi.version3.network.map

import com.uogames.clientApi.version3.network.response.UserViewResponse
import com.uogames.dto.global.GlobalUserView

object UserViewMap : Map<UserViewResponse, GlobalUserView> {

    override fun UserViewResponse.toDTO() = GlobalUserView(
        globalOwner = globalOwner,
        name = name
    )

    override fun GlobalUserView.toResponse() = UserViewResponse(
        globalOwner = globalOwner,
        name = name
    )

}