package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.DownloadDAO
import com.uogames.database.entity.DownloadEntity
import com.uogames.database.withNotNull
import kotlinx.coroutines.flow.Flow

class DownloadRepository(
	private val dao: DownloadDAO
) {

	suspend fun insert(downloadEntity: DownloadEntity) = dao.insert(downloadEntity)

	suspend fun delete(downloadEntity: DownloadEntity) = dao.delete(downloadEntity)

	suspend fun count() = dao.count()

	fun countFlow() = dao.countFlow()

	suspend fun getFirst() = dao.getFirst()

	suspend fun clean() = dao.clean()

	fun existsFlow(
		id: Int? = null,
		phraseId: String? = null,
		cardId: String? = null,
		moduleId: String? = null
	): Flow<Boolean> {
		val builder = StringBuilder()
		val params = ArrayList<Any>()
		builder.append("SELECT EXISTS (")
		builder.append("SELECT id FROM download_table ")
		val r = withNotNull(id, phraseId, cardId, moduleId)
		if (r) builder.append("WHERE ")
		id?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("id = ? ")
			params.add(id)
		}
		phraseId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_phrase_id = ? ")
			params.add(phraseId)
		}
		cardId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_card_id = ? ")
			params.add(cardId)
		}
		moduleId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_module_id = ? ")
			params.add(moduleId)
		}
		builder.append(")")
		return dao.existsFlow(SimpleSQLiteQuery(builder.toString(), params.toArray()))
	}

	suspend fun exists(
		id: Int? = null,
		phraseId: String? = null,
		cardId: String? = null,
		moduleId: String? = null
	): Boolean {
		val builder = StringBuilder()
		val params = ArrayList<Any>()
		builder.append("SELECT EXISTS (")
		builder.append("SELECT id FROM download_table ")
		val r = withNotNull(id, phraseId, cardId, moduleId)
		if (r) builder.append("WHERE ")
		id?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("id = ? ")
			params.add(id)
		}
		phraseId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_phrase_id = ? ")
			params.add(phraseId)
		}
		cardId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_card_id = ? ")
			params.add(cardId)
		}
		moduleId?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("global_module_id = ? ")
			params.add(moduleId)
		}
		builder.append(")")
		return dao.exists(SimpleSQLiteQuery(builder.toString(), params.toArray()))
	}
}