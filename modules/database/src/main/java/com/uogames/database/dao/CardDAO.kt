package com.uogames.database.dao

import android.database.sqlite.SQLiteQuery
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@Dao
interface CardDAO {

	@Insert
	suspend fun insert(cardEntity: CardEntity): Long

	@Delete
	suspend fun delete(cardEntity: CardEntity): Int

	@Update
	suspend fun update(cardEntity: CardEntity): Int

	@RawQuery
	suspend fun count(query: SupportSQLiteQuery): Int

	@RawQuery
	suspend fun get(query: SupportSQLiteQuery): CardEntity?

	@Query(
		"SELECT COUNT(nct.id) FROM cards_table AS nct " +
				"JOIN phrase_table AS pt1 " +
				"ON pt1.id = nct.id_phrase " +
				"JOIN phrase_table AS pt2 " +
				"ON pt2.id = nct.id_translate " +
				"WHERE pt1.phrase LIKE '%' || :like || '%' " +
				"OR pt2.phrase LIKE '%' || :like || '%'"
	)
	fun getCountFlow(like: String): Flow<Int>

	@Query(
		"SELECT COUNT(nct.id) FROM cards_table AS nct " +
				"JOIN phrase_table AS pt1 " +
				"ON pt1.id = nct.id_phrase " +
				"JOIN phrase_table AS pt2 " +
				"ON pt2.id = nct.id_translate " +
				"WHERE (pt1.phrase LIKE '%' || :like || '%' " +
				"OR pt2.phrase LIKE '%' || :like || '%') "
	)
	fun test(like: String): Flow<Int>

	@Query("SELECT COUNT(id) FROM cards_table")
	fun getCountFlow(): Flow<Int>

	@Query(
		"SELECT nct.* FROM cards_table AS nct " +
				"JOIN phrase_table AS pt1 " +
				"ON pt1.id = nct.id_phrase " +
				"JOIN phrase_table AS pt2 " +
				"ON pt2.id = nct.id_translate " +
				"WHERE pt1.phrase LIKE '%' || :like || '%' " +
				"OR pt2.phrase LIKE '%' || :like || '%' " +
				"LIMIT :number, 1"
	)
	fun getCardFlow(like: String, number: Int): Flow<CardEntity?>


	@Query(
		"SELECT nct.* FROM cards_table AS nct " +
				"JOIN phrase_table AS pt1 " +
				"ON pt1.id = nct.id_phrase " +
				"JOIN phrase_table AS pt2 " +
				"ON pt2.id = nct.id_translate " +
				"WHERE pt1.phrase LIKE '%' || :like || '%' " +
				"OR pt2.phrase LIKE '%' || :like || '%' " +
				"LIMIT :number, 1"
	)
	suspend fun getCard(like: String, number: Int): CardEntity?

	@Query(
		"SELECT nct.*, pt1.phrase AS ph1, pt2.phrase AS ph2 FROM cards_table AS nct " +
				"JOIN phrase_table AS pt1 " +
				"ON pt1.id = nct.id_phrase " +
				"JOIN phrase_table AS pt2 " +
				"ON pt2.id = nct.id_translate " +
				"WHERE pt1.phrase LIKE '%' || :like || '%' " +
				"OR pt2.phrase LIKE '%' || :like || '%' " +
				"ORDER BY length(ph1), ph1, length(ph2), ph2 " +
				"LIMIT :number, 1"
	)
	suspend fun test(like: String, number: Int): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id = :id")
	suspend fun getById(id: Int): CardEntity?

	@Query("SELECT * FROM cards_table WHERE global_id = :id")
	suspend fun getByGlobalId(id: UUID): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<CardEntity?>

	@Query("SELECT * FROM cards_table ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandom(): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id <> :id ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithOut(id: Int): CardEntity?

	@Query("SELECT DISTINCT(reason) FROM cards_table WHERE reason LIKE '%' || :text || '%' ORDER BY LENGTH(reason), reason LIMIT 5")
	suspend fun getClues(text:String): List<String>

}