package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*


data class GlobalModule(
	val globalId: UUID = DefaultUUID.value,
	val globalOwner: String,
	val name: String = "",
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0,

	)
