package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*

data class GlobalReport(
    val globalId: UUID = DefaultUUID.value,
    var claimant: String,
    var message: String,
    var accused: String,
    var idPhrase: UUID? = null,
    var idCard: UUID? = null,
    var idModule: UUID? = null
)