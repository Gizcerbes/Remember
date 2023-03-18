package com.uogames.dto.global

import java.util.*

data class GlobalCardView(
    val globalId: UUID,
    var user: GlobalUserView,
    var phrase: GlobalPhraseView,
    var translate: GlobalPhraseView,
    var image: GlobalImageView? = null,
    var reason: String = "",
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
)