package com.uogames.dto.global

import java.util.*

data class GlobalImageView(
    val globalId: UUID,
    val user: GlobalUserView,
    var imageUri: String
)