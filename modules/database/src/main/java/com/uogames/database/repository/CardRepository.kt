package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.CardDAO
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.dto.local.LocalCard
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.ArrayList

class CardRepository(private val dao: CardDAO) {

    suspend fun insert(card: LocalCard) = dao.insert(card.toEntity())

    suspend fun delete(card: LocalCard) = dao.delete(card.toEntity()) > 0

    suspend fun update(card: LocalCard) = dao.update(card.toEntity()) > 0

    suspend fun count(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null
    ): Int {
        val builder = StringBuilder()
        val params = ArrayList<Any>()
        builder.append("SELECT COUNT(nct.id) FROM cards_table AS nct ")
        builder.append("JOIN phrase_table AS pt1 ")
        builder.append("ON pt1.id = nct.id_phrase ")
        builder.append("JOIN phrase_table AS pt2 ")
        builder.append("ON pt2.id = nct.id_translate ")
        if (like != null || langFirst != null || langSecond != null || countryFirst != null || countrySecond != null) {
            builder.append("WHERE ")
        }
        like?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("(pt1.phrase LIKE '%' || ? || '%' ")
            builder.append("OR pt2.phrase LIKE '%' || ? || '%') ")
            params.add(like)
            params.add(like)
        }
        langFirst?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.lang =  ? ")
            params.add(langFirst)
        }
        langSecond?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.lang =  ? ")
            params.add(langSecond)
        }
        countryFirst?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country =  ? ")
            params.add(countryFirst)
        }
        countrySecond?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country =  ? ")
            params.add(countrySecond)
        }
        return dao.count(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    suspend fun get(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        position: Int? = null
    ): LocalCard? {
        val builder = StringBuilder()
        val params = ArrayList<Any>()
        builder.append("SELECT nct.* FROM cards_table AS nct ")
        builder.append("JOIN phrase_table AS pt1 ")
        builder.append("ON pt1.id = nct.id_phrase ")
        builder.append("JOIN phrase_table AS pt2 ")
        builder.append("ON pt2.id = nct.id_translate ")
        if (like != null || langFirst != null || langSecond != null || countryFirst != null || countrySecond != null) {
            builder.append("WHERE ")
        }
        like?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("(pt1.phrase LIKE '%' || ? || '%' ")
            builder.append("OR pt2.phrase LIKE '%' || ? || '%') ")
            params.add(like)
            params.add(like)
        }
        langFirst?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.lang =  ? ")
            params.add(langFirst)
        }
        langSecond?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt2.lang =  ? ")
            params.add(langSecond)
        }
        countryFirst?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country =  ? ")
            params.add(countryFirst)
        }
        countrySecond?.let {
            if (params.isNotEmpty()) builder.append("AND ")
            builder.append("pt1.country =  ? ")
            params.add(countrySecond)
        }
        position?.let { builder.append("LIMIT $position, 1") }
        return dao.get(SimpleSQLiteQuery(builder.toString(),params.toArray()))?.toDTO()
    }

    fun getCountFlow() = dao.getCountFlow()

    suspend fun getById(id: Int) = dao.getById(id)?.toDTO()

    suspend fun getByGlobalId(id: UUID) = dao.getByGlobalId(id)?.toDTO()

    fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

    suspend fun getRandom() = dao.getRandom()?.toDTO()

    suspend fun getRandomWithOut(id: Int) = dao.getRandomWithOut(id)?.toDTO()


}