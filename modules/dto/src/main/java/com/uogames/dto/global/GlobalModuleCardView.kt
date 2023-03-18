package com.uogames.dto.global

import java.util.*

data class GlobalModuleCardView(
    val globalId: UUID,
    var user: GlobalUserView,
    var module: GlobalModuleView,
    var card: GlobalCardView
)