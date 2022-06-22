package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.PronunciationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PronunciationDAO {

	@Insert
	suspend fun insert(pronunciationEntity: PronunciationEntity): Long

	@Delete
	suspend fun delete(pronunciationEntity: PronunciationEntity): Int

	@Update
	suspend fun update(pronunciationEntity: PronunciationEntity): Int

	@Query("SELECT COUNT(id) FROM pronounce_table")
	fun countFlow(): Flow<Int>

	@Query("SELECT * FROM pronounce_table WHERE id =:id")
	fun getByIdFlow(id: Int): Flow<PronunciationEntity?>

	@Query("SELECT * FROM pronounce_table LIMIT :number, 1")
	fun getByNumber(number: Int): Flow<PronunciationEntity?>


	@Query(
		"SELECT * FROM pronounce_table " +
				"WHERE " +
				"NOT EXISTS (SELECT * FROM phrase_table pt WHERE pt.idPronounce = pronounce_table.id )"
	)
	suspend fun freePronounce(): List<PronunciationEntity>

}