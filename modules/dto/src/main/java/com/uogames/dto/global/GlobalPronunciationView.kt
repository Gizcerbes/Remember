package com.uogames.dto.global

import java.util.*

data class GlobalPronunciationView(
    val globalId: UUID,
    val user: GlobalUserView,
    var audioUri: String
)