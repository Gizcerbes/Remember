package com.uogames.repository.map

import com.uogames.database.entity.DownloadEntity
import com.uogames.dto.local.LocalDownload
import java.util.UUID

object DownloadMap {

	fun DownloadEntity.toDTO() = LocalDownload(
		id = id,
		globalPhraseId = globalPhraseId?.let { UUID.fromString(it) },
		globalCardId = globalCardId?.let { UUID.fromString(globalCardId) },
		globalModuleId = globalModuleId?.let { UUID.fromString(globalModuleId) }
	)

	fun LocalDownload.toEntity() = DownloadEntity(
		id = id,
		globalPhraseId = globalPhraseId?.toString(),
		globalCardId = globalCardId?.toString(),
		globalModuleId = globalModuleId?.toString()
	)

}