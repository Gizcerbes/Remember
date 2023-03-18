package com.uogames.dto.local

import java.util.*

data class LocalModuleCard(
	val id: Int = 0,
	val idModule: Int,
	val idCard: Int,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)

data class LocalModuleCardView(
	val id: Int = 0,
	val module: LocalModuleView,
	val card: LocalCardView,
	val globalId: UUID? = null,
	val globalOwner: String? = null
)