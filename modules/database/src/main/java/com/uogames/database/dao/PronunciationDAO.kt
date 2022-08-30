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
	suspend fun count(): Int

	@Query("SELECT COUNT(id) FROM pronounce_table")
	fun countFlow(): Flow<Int>

	@Query("SELECT * FROM pronounce_table WHERE id = :id")
	suspend fun getById(id: Int): PronunciationEntity?

	@Query("SELECT * FROM pronounce_table WHERE global_id = :id")
	suspend fun getByGlobalId(id: Long): PronunciationEntity?

	@Query("SELECT * FROM pronounce_table WHERE id =:id")
	fun getByIdFlow(id: Int): Flow<PronunciationEntity?>

	@Query("SELECT * FROM pronounce_table LIMIT :number, 1")
	suspend fun getByNumber(number: Int): PronunciationEntity?

	@Query("SELECT * FROM pronounce_table LIMIT :number, 1")
	fun getByNumberFlow(number: Int): Flow<PronunciationEntity?>

	@Query(
		"SELECT * FROM pronounce_table " +
				"WHERE " +
				"NOT EXISTS (SELECT * FROM phrase_table pt WHERE pt.id_pronounce = pronounce_table.id )"
	)
	suspend fun freePronounce(): List<PronunciationEntity>

}