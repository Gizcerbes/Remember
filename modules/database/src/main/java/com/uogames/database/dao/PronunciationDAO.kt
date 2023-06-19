package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.PronunciationEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

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
	suspend fun getByGlobalId(id: UUID): PronunciationEntity?

	@Query("SELECT * FROM pronounce_table WHERE id =:id")
	fun getByIdFlow(id: Int): Flow<PronunciationEntity?>

	@Query("SELECT * FROM pronounce_table LIMIT :number, 1")
	suspend fun getByNumber(number: Int): PronunciationEntity?

	@Query("SELECT * FROM pronounce_table LIMIT :number, 1")
	fun getByNumberFlow(number: Int): Flow<PronunciationEntity?>

//	@Query(
//		"SELECT * FROM pronounce_table " +
//				"WHERE " +
//				"NOT EXISTS (SELECT * FROM phrase_table pt WHERE pt.id_pronounce = pronounce_table.id )"
//	)
//	suspend fun freePronounce(): List<PronunciationEntity>

	@Query(
		"SELECT prt.* FROM pronounce_table AS prt " +
				"LEFT JOIN phrase_table AS pht " +
				"ON prt.id = pht.id_pronounce " +
				"WHERE pht.id_pronounce IS NULL"
	)
	suspend fun freePronounce(): List<PronunciationEntity>

}