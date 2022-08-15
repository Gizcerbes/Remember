package com.uogames.dto.global


data class Module(
	val globalId: Long = 0,
	val globalOwner: String,
	val name: String = "",
	val timeChange: Long = 0,
	val like: Long = 0,
	val dislike: Long = 0,

)
