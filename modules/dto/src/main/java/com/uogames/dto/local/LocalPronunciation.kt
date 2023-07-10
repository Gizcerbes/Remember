package com.uogames.dto.local

import java.util.*

data class LocalPronunciation(
	val id: Int = 0,
	val audioUri: String = "",
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null
)

data class LocalPronunciationView(
	val id: Int,
	val audioUri: String,
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null
)