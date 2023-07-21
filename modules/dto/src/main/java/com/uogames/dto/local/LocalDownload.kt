package com.uogames.dto.local

import java.util.UUID

data class LocalDownload(
	val id: Int = 0,
	val globalPhraseId: UUID? = null,
	val globalCardId: UUID? = null,
	val globalModuleId: UUID? = null
)