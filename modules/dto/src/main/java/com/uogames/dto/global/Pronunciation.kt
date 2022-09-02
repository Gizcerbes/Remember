package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*


data class Pronunciation(
	val globalId: UUID = DefaultUUID.value,
	val globalOwner: String,
	val audioUri: String
)