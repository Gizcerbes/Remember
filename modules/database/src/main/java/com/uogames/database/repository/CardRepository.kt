package com.uogames.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.uogames.database.dao.CardDAO
import com.uogames.database.entity.CardEntity
import java.util.*
import kotlin.collections.ArrayList

class CardRepository(
	private val cardDAO: CardDAO
) {

	suspend fun insert(card: CardEntity) = cardDAO.insert(card)

	suspend fun delete(card: CardEntity) = cardDAO.delete(card) > 0

	suspend fun update(card: CardEntity) = cardDAO.update(card) > 0

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
		newest: Boolean = false,
		position: Int? = null
	): CardEntity? {
		val builder = StringBuilder()
		val params = ArrayList<Any>()
		builder.append("SELECT nct.*, pt1.phrase AS ph1, pt2.phrase AS ph2 FROM cards_table AS nct ")
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
		if (newest) builder.append("ORDER BY nct.time_change DESC ")
		else builder.append("ORDER BY length(ph1), ph1, length(ph2), ph2 ")
		position?.let { builder.append("LIMIT $position, 1") }
		return cardDAO.get(SimpleSQLiteQuery(builder.toString(), params.toArray()))
	}

	suspend fun get(
		like: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = getEntity(like, langFirst, langSecond, countryFirst, countrySecond, newest, position)

	fun getCountFlow() = cardDAO.getCountFlow()

	suspend fun getById(id: Int) = cardDAO.getById(id)

	suspend fun getViewById(id: Int) = cardDAO.getById(id)

	suspend fun getByGlobalId(id: UUID) = cardDAO.getByGlobalId(id)

	fun getByIdFlow(id: Int) = cardDAO.getByIdFlow(id)

	suspend fun getRandom() = cardDAO.getRandom()

	suspend fun getRandomView() = cardDAO.getRandom()

	suspend fun getRandomViewWithout(cardIds: Array<Int>) = cardDAO.getRandomWithout(cardIds)

	suspend fun getRandomViewWithoutPhrases(phraseIds: Array<Int>) = cardDAO.getRandomWithoutPhrases(phraseIds)

	suspend fun getUnknowableView() = cardDAO.getUnknowable()

	suspend fun getConfusingView(idPhrase: Int) = cardDAO.getConfusing(idPhrase)

	suspend fun getConfusingViewWithoutPhrases(idPhrase: Int, phraseIds: Array<Int>) =
		cardDAO.getConfusingWithoutPhrases(idPhrase, phraseIds)

	suspend fun getRandomWithOut(id: Int) = cardDAO.getRandomWithOut(id)

	suspend fun getClues(text: String) = cardDAO.getClues(text)

	fun countFree() = cardDAO.countFree()

	suspend fun deleteFree() = cardDAO.deleteFree()

	fun isChanged(id: Int) = cardDAO.isChanged(id)



}