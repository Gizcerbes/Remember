package com.uogames.dto.local

data class Image(
	val id: Int = 0,
	val imgUri: String = "",
	val globalId: Long? = null,
	val globalOwner: String? = null
)