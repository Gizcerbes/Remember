package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.PhraseDAO
import com.uogames.database.entity.PhraseEntity
import java.util.*
import kotlin.collections.ArrayList

class PhraseRepository(
    private val dao: PhraseDAO,
) {

    suspend fun add(phrase: PhraseEntity) = dao.insert(phrase)

    suspend fun delete(phrase: PhraseEntity) = dao.delete(phrase) > 0

    suspend fun update(phrase: PhraseEntity) = dao.update(phrase) > 0

    fun countFlow() = dao.countFLOW()

    private suspend fun getEntity(
        like: String? = null,
        lang: String? = null,
        country: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ): PhraseEntity? {
        val builder = StringBuilder()
        val params = ArrayList<Any>()
        builder.append("SELECT phrase_table.*, length(phrase_table.phrase) AS len  FROM phrase_table ")
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
        if (newest) builder.append("ORDER BY time_change DESC ")
        else builder.append("ORDER BY len, phrase ASC ")
        position?.let { builder.append("LIMIT $position, 1") }
        return dao.get(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    suspend fun get(
        like: String? = null,
        lang: String? = null,
        country: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = getEntity(like, lang, country, newest, position)

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

    suspend fun getById(id: Int) = dao.getById(id)

    suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)

    fun getByIdFlow(id: Int) = dao.getByIdFlow(id)

    fun countFree() = dao.countFree()

    suspend fun deleteFree() = dao.deleteFree()

    fun isChanged(id: Int) = dao.isChanged(id)

}