package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.CardDAO
import com.uogames.database.entity.CardEntity
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.database.map.ViewMap
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.ArrayList

class CardRepository(
    private val cardDAO: CardDAO,
    private val map: ViewMap<CardEntity,LocalCardView>
) {

    suspend fun insert(card: LocalCard) = cardDAO.insert(card.toEntity())

    suspend fun delete(card: LocalCard) = cardDAO.delete(card.toEntity()) > 0

    suspend fun update(card: LocalCard) = cardDAO.update(card.toEntity()) > 0

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
        return cardDAO.count(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    private suspend fun getEntity(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        position: Int? = null
    ): CardEntity? {
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
        return cardDAO.get(SimpleSQLiteQuery(builder.toString(), params.toArray()))
    }

    suspend fun get(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        position: Int? = null
    ) = getEntity(like, langFirst, langSecond, countryFirst, countrySecond, position)?.toDTO()

    fun getCountFlow() = cardDAO.getCountFlow()

    suspend fun getById(id: Int) = cardDAO.getById(id)?.toDTO()

    suspend fun getViewById(id: Int) = cardDAO.getById(id)?.let { map.toDTO(it) }

    suspend fun getByGlobalId(id: UUID) = cardDAO.getByGlobalId(id)?.toDTO()

    fun getByIdFlow(id: Int) = cardDAO.getByIdFlow(id).map { it?.toDTO() }

    suspend fun getRandom() = cardDAO.getRandom()?.toDTO()

    suspend fun getRandomWithOut(id: Int) = cardDAO.getRandomWithOut(id)?.toDTO()


}