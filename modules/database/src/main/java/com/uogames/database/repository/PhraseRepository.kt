package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.PhraseDAO
import com.uogames.database.entity.PhraseEntity
import com.uogames.database.map.PhraseMap.toDTO
import com.uogames.database.map.PhraseMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.ArrayList

class PhraseRepository(
    private val dao: PhraseDAO,
    private val map: ViewMap<PhraseEntity, LocalPhraseView>
) {

    suspend fun add(phrase: LocalPhrase) = dao.insert(phrase.toEntity())

    suspend fun delete(phrase: LocalPhrase) = dao.delete(phrase.toEntity()) > 0

    suspend fun update(phrase: LocalPhrase) = dao.update(phrase.toEntity()) > 0

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
    ) = getEntity(like, lang, country, newest, position)?.toDTO()

    suspend fun getView(
        like: String? = null,
        lang: String? = null,
        country: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = getEntity(like, lang, country, newest, position)?.let { map.toDTO(it) }

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

    suspend fun getViewById(id: Int) = dao.getById(id)?.let { map.toDTO(it) }

    suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)?.toDTO()

    fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

    fun countFree() = dao.countFree()

    suspend fun deleteFree() = dao.deleteFree()

    fun isChanged(id: Int) = dao.isChanged(id)

}