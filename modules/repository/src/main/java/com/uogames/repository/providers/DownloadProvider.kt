package com.uogames.repository.providers

import com.uogames.database.repository.DownloadRepository
import com.uogames.dto.local.LocalDownload
import com.uogames.repository.map.DownloadMap.toDTO
import com.uogames.repository.map.DownloadMap.toEntity
import java.util.UUID

class DownloadProvider(
	private val rep: DownloadRepository
) {

	suspend fun insert(download: LocalDownload) = rep.insert(download.toEntity())

	suspend fun delete(download: LocalDownload) = rep.delete(download.toEntity())

	suspend fun count() = rep.count()

	fun countFlow() = rep.countFlow()

	suspend fun getFirst() = rep.getFirst()?.toDTO()

	suspend fun clean() = rep.clean()

	fun existsFlow(
		id: Int? = null,
		phraseId: UUID? = null,
		cardId: UUID? = null,
		moduleId: UUID? = null
	) = rep.existsFlow(
		id = id,
		phraseId = phraseId?.toString(),
		cardId = cardId?.toString(),
		moduleId = moduleId?.toString()
	)

	suspend fun exists(
		id: Int? = null,
		phraseId: UUID? = null,
		cardId: UUID? = null,
		moduleId: UUID? = null
	) = rep.exists(
		id = id,
		phraseId = phraseId?.toString(),
		cardId = cardId?.toString(),
		moduleId = moduleId?.toString()
	)

}