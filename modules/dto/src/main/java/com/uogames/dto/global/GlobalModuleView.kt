package com.uogames.dto.global

import java.util.*

data class GlobalModuleView(
    val globalId: UUID,
    var user: GlobalUserView,
    var name: String = "",
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
)