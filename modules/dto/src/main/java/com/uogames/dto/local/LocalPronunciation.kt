package com.uogames.dto.local

import java.util.*

data class LocalPronunciation(
	val id: Int,
	val audioUri: String,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)