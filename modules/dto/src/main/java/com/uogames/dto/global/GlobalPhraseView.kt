package com.uogames.dto.global

import java.util.*

data class GlobalPhraseView(
    val globalId: UUID,
    var user: GlobalUserView,
    var phrase: String = "",
    var definition: String? = null,
    var lang: String = "eng",
    var country: String = "BELARUS",
    var pronounce: GlobalPronunciationView? = null,
    var image: GlobalImageView? = null,
    var timeChange: Long = 0,
    var like: Long = 0,
    var dislike: Long = 0
)