package com.uogames.dto.local

data class ModuleCard(
	val id: Int = 0,
	val idModule: Int,
	val idCard: Int,
	val globalId: Long? = null,
	val globalOwner: String? = null
)