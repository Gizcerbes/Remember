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
		"SELECT COUNT(*) FROM (" +
				"SELECT nct.id FROM new_cards_table nct " +
				"INNER JOIN phrase_table pt ON nct.idPhrase = pt.id " +
				"WHERE pt.phrase LIKE '%' || :like || '%' " +
				"UNION " +
				"SELECT nct.id FROM new_cards_table nct " +
				"INNER JOIN phrase_table pt ON nct.idTranslate = pt.id " +
				"WHERE pt.phrase LIKE '%' || :like || '%' )"
	)
	fun getCountFlow(like: String): Flow<Int>

	@Query(
		"SELECT nct.* FROM new_cards_table nct " +
				"INNER JOIN phrase_table pt ON nct.idPhrase = pt.id " +
				"WHERE pt.phrase LIKE '%' || :like || '%' " +
				"UNION " +
				"SELECT nct.* FROM new_cards_table nct " +
				"INNER JOIN phrase_table pt ON nct.idTranslate = pt.id " +
				"WHERE pt.phrase LIKE '%' || :like || '%'" +
				"LIMIT :number, 1"
	)
	fun getCardFlow(like: String, number: Int): Flow<CardEntity?>

	@Query("SELECT * FROM new_cards_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<CardEntity?>


}