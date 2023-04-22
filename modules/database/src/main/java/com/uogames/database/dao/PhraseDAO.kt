package com.uogames.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PhraseDAO {

	@Insert
	suspend fun insert(wordEntity: PhraseEntity): Long

	@Delete
	suspend fun delete(wordEntity: PhraseEntity): Int

	@Update
	suspend fun update(wordEntity: PhraseEntity): Int

	@RawQuery
	suspend fun count(query: SupportSQLiteQuery): Int

	@RawQuery
	suspend fun get(query: SupportSQLiteQuery): PhraseEntity?

	@Query("SELECT COUNT(phrase) FROM phrase_table")
	fun countFLOW(): Flow<Int>

	@Query("SELECT * FROM phrase_table WHERE id = :id")
	suspend fun getById(id: Int): PhraseEntity?

	@Query("SELECT * FROM phrase_table WHERE global_id = :id")
	suspend fun getByGlobalId(id: UUID): PhraseEntity?

	@Query("SELECT * FROM phrase_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<PhraseEntity?>

	@Query("SELECT phrase_table.*, length(phrase_table.phrase) AS len FROM phrase_table WHERE id = :id ORDER BY time_change DESC")
	fun getTest(id: Int): Flow<PhraseEntity?>


}