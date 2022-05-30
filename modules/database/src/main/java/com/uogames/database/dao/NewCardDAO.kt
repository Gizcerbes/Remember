package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.NewCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewCardDAO {

	@Insert
	suspend fun insert(newCardEntity: NewCardEntity)

	@Delete
	suspend fun delete(newCardEntity: NewCardEntity)

	@Update
	suspend fun update(newCardEntity: NewCardEntity)

	@Query("SELECT COUNT(id) FROM new_cards_table " +
			"WHERE (word LIKE '%' || :nameItems || '%' " +
			"OR translate LIKE '%' || :nameItems || '%')" +
			"AND (langFrom)")
	fun countFLOW(nameItems: String, langFrom: String, langTo: String): Flow<Int>


}