package com.uogames.dto.local

import java.util.*

data class Module(
	val id: Int = 0,
	val name: String = "",
	val owner: String = "",
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)