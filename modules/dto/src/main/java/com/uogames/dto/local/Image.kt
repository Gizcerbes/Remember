package com.uogames.dto.local

import java.util.*

data class Image(
	val id: Int = 0,
	val imgUri: String = "",
	val globalId: UUID? = null,
	val globalOwner: String? = null
)