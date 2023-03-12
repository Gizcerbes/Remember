package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.PhraseDAO
import com.uogames.database.map.PhraseMap.toDTO
import com.uogames.database.map.PhraseMap.toEntity
import com.uogames.dto.local.LocalPhrase
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.ArrayList

class PhraseRepository(private val dao: PhraseDAO) {

	suspend fun add(phrase: LocalPhrase) = dao.insert(phrase.toEntity())

	suspend fun delete(phrase: LocalPhrase) = dao.delete(phrase.toEntity()) > 0

	suspend fun update(phrase: LocalPhrase) = dao.update(phrase.toEntity()) > 0

	fun countFlow() = dao.countFLOW()

	suspend fun get(
        like: String? = null,
        lang: String? = null,
        country: String? = null,
        position: Int? = null
    ): LocalPhrase? {
		val builder = StringBuilder()
        val params = ArrayList<Any>()
		builder.append("SELECT * FROM phrase_table ")
		if (like != null || lang != null || country != null) builder.append("WHERE ")
        like?.let {
            builder.append("phrase LIKE '%' || ? || '%' ")
            params.add(like)
        }
        lang?.let {
			if (params.isNotEmpty()) builder.append("AND ")
            builder.append("lang = ? ")
            params.add(lang)

        }
        country?.let {
			if (params.isNotEmpty()) builder.append("AND ")
            builder.append("country = ? ")
            params.add(country)
        }
        position?.let { builder.append("LIMIT $position, 1") }
		return dao.get(SimpleSQLiteQuery(builder.toString(), params.toArray()))?.toDTO()
	}

	suspend fun count(
		like: String? = null,
		lang: String? = null,
		country: String? = null
	): Int {
		val builder = StringBuilder()
		val params = ArrayList<Any>()
		builder.append("SELECT COUNT(id) FROM phrase_table ")
		if (like != null || lang != null || country != null) builder.append("WHERE ")
		like?.let {
			builder.append("phrase LIKE '%' || ? || '%' ")
			params.add(like)
		}
		lang?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("lang = ? ")
			params.add(lang)
		}
		country?.let {
			if (params.isNotEmpty()) builder.append("AND ")
			builder.append("country = ? ")
			params.add(country)
		}
		return dao.count(SimpleSQLiteQuery(builder.toString(), params.toArray()))
	}

	suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

	suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)?.toDTO()

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

}