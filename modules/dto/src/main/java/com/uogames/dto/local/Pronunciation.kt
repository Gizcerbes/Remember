package com.uogames.dto.local

data class Pronunciation(
	val id: Int,
	val audioUri: String,
	val globalId: Long? = null,
	val globalOwner: String? = null
)