package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {

	@Insert
	suspend fun insert(cardEntity: CardEntity): Long

	@Delete
	suspend fun delete(cardEntity: CardEntity): Int

	@Update
	suspend fun update(cardEntity: CardEntity): Int

	@Query(
		"SELECT COUNT(id) FROM cards_table nct " +
				"WHERE EXISTS (SELECT id FROM phrase_table pt WHERE nct.id_phrase = pt.id AND pt.phrase LIKE '%' || :like || '%') " +
				"OR EXISTS (SELECT id FROM phrase_table pt WHERE nct.id_translate = pt.id AND pt.phrase LIKE '%' || :like || '%') "
	)
	fun getCountFlow(like: String): Flow<Int>

	@Query("SELECT COUNT(id) FROM cards_table")
	fun getCountFlow(): Flow<Int>

	@Query(
		"SELECT * FROM cards_table nct " +
				"WHERE EXISTS (SELECT id FROM phrase_table pt WHERE nct.id_phrase = pt.id AND pt.phrase LIKE '%' || :like || '%') " +
				"OR EXISTS (SELECT id FROM phrase_table pt WHERE nct.id_translate = pt.id AND pt.phrase LIKE '%' || :like || '%') " +
				"LIMIT :number, 1"
	)
	fun getCardFlow(like: String, number: Int): Flow<CardEntity?>

	@Query("SELECT * FROM cards_table WHERE id = :id")
	suspend fun getById(id: Int): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<CardEntity?>

	@Query("SELECT * FROM cards_table ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandom(): CardEntity?

	@Query("SELECT * FROM cards_table WHERE id <> :id ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithOut(id: Int): CardEntity?

}