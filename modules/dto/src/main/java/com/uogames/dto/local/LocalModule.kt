package com.uogames.dto.local

import java.util.*

data class LocalModule(
	val id: Int = 0,
	val name: String = "",
	val owner: String = "",
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null,
	var changed: Boolean = false
)

data class LocalModuleView(
	val id: Int = 0,
	val name: String = "",
	val owner: String = "",
	val timeChange: Long = Date().time,
	val like: Long = 0,
	val dislike: Long = 0,
	val globalId: UUID = UUID.randomUUID(),
	val globalOwner: String? = null,
	val changed: Boolean = false
)