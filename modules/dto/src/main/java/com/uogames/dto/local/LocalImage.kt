package com.uogames.dto.local

import java.util.*

data class LocalImage(
	val id: Int = 0,
	val imgUri: String = "",
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null
)

data class LocalImageView(
	val id: Int = 0,
	val imgUri: String = "",
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null
)