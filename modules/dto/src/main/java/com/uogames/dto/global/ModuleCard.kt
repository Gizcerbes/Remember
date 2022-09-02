package com.uogames.dto.global

import com.uogames.dto.DefaultUUID
import java.util.*


data class ModuleCard(
	val globalId: UUID = DefaultUUID.value,
	val globalOwner: String,
	val idModule: UUID,
	val idCard: UUID
)