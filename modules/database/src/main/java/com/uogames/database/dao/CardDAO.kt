package com.uogames.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow
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

	@RawQuery(observedEntities = [CardEntity::class])
	fun countFlow(query: SupportSQLiteQuery): Flow<Int>

	@RawQuery
	suspend fun get(query: SupportSQLiteQuery): CardEntity?

	@RawQuery(observedEntities = [CardEntity::class])
	fun getFlow(query: SupportSQLiteQuery): Flow<CardEntity?>

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

	@Query("SELECT * FROM cards_table WHERE id = :id")
	suspend fun getById(id: Int): CardEntity?

	@Query("SELECT * FROM cards_table WHERE global_id = :id")
	suspend fun getByGlobalId(id: UUID): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<CardEntity?>

	@Query("SELECT * FROM cards_table ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandom(): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id NOT IN (:cardsIds) ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithout(cardsIds: Array<Int>): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id_phrase NOT IN (:phraseIds) AND id_translate NOT IN (:phraseIds) ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithoutPhrases(phraseIds: Array<Int>): CardEntity?

	@Query("SELECT ct.* FROM cards_table AS ct " +
			"LEFT JOIN error_card AS ec ON ct.id_phrase = ec.id_phrase AND ct.id_translate = ec.id_translate " +
			"ORDER BY CASE " +
			"WHEN ec.percent_correct IS NULL THEN 100 " +
			"ELSE ec.percent_correct " +
			"END ASC " +
			"LIMIT 1")
	suspend fun getUnknowable(): CardEntity?

	@Query("SELECT ct.* FROM cards_table AS ct " +
			"LEFT JOIN error_card AS ec ON  ct.id_translate = ec.id_translate " +
			"WHERE ec.id_phrase = :idPhrase " +
			"ORDER BY  ec.percent_correct ASC " +
			"LIMIT 1")
	suspend fun getConfusing(idPhrase: Int): CardEntity?

	@Query("SELECT ct.* FROM cards_table AS ct " +
			"LEFT JOIN error_card AS ec ON  ct.id_translate = ec.id_translate " +
			"WHERE ec.id_phrase = :idPhrase " +
			"AND ct.id_translate NOT IN (:phraseIds) " +
			"ORDER BY ec.percent_correct  ASC " +
			"LIMIT 1")
	suspend fun getConfusingWithoutPhrases(idPhrase: Int, phraseIds: Array<Int>): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id <> :id ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithOut(id: Int): CardEntity?

	@Query("SELECT DISTINCT(reason) FROM cards_table WHERE reason LIKE '%' || :text || '%' ORDER BY LENGTH(reason), reason LIMIT 5")
	suspend fun getClues(text:String): List<String>

	@Query("SELECT COUNT(DISTINCT ct.id) FROM cards_table AS ct " +
			"LEFT JOIN module_card AS mc " +
			"ON ct.id = mc.id_card " +
			"WHERE mc.id IS NULL")
	fun countFree(): Flow<Int>

	@Query("DELETE FROM cards_table " +
			"WHERE NOT EXISTS (SELECT mct.id FROM module_card AS mct WHERE cards_table.id = mct.id_card)")
	suspend fun deleteFree()


	@Query("SELECT changed FROM cards_table WHERE id = :id")
	fun isChanged(id: Int): Flow<Boolean?>
}