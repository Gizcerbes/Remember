package com.uogames.dto.local

import java.util.*

data class ModuleCard(
	val id: Int = 0,
	val idModule: Int,
	val idCard: Int,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)