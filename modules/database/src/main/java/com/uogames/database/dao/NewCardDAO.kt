package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.NewCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewCardDAO {

	@Insert
	suspend fun insert(newCardEntity: NewCardEntity): Long

	@Delete
	suspend fun delete(newCardEntity: NewCardEntity): Int

	@Update
	suspend fun update(newCardEntity: NewCardEntity): Int

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
	fun getCardFlow(like: String, number: Int): Flow<NewCardEntity?>

	@Query("SELECT * FROM new_cards_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<NewCardEntity?>


}