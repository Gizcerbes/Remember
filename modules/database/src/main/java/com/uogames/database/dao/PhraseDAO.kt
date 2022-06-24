package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDAO {

	@Insert
	suspend fun insert(wordEntity: PhraseEntity): Long

	@Delete
	suspend fun delete(wordEntity: PhraseEntity): Int

	@Update
	suspend fun update(wordEntity: PhraseEntity): Int

	@Query("SELECT COUNT(phrase) FROM phrase_table")
	fun countFLOW(): Flow<Int>

	@Query("SELECT COUNT(id) FROM phrase_table WHERE phrase LIKE '%' || :like || '%'")
	fun countFlow(like: String): Flow<Int>

	@Query("SELECT COUNT(id) FROM phrase_table WHERE phrase LIKE '%' || :like || '%' AND lang = :lang")
	fun countFlow(like: String, lang: String): Flow<Int>

	@Query("SELECT * FROM phrase_table LIMIT :position , 1 ")
	fun getFlow(position: Int): Flow<PhraseEntity?>

	@Query("SELECT * FROM phrase_table WHERE phrase LIKE '%' || :like || '%' LIMIT :position, 1")
	fun getFlow(like: String, position: Int): Flow<PhraseEntity?>

	@Query("SELECT * FROM phrase_table WHERE phrase LIKE '%' || :like || '%' AND lang = :lang LIMIT :position, 1")
	fun getFlow(like: String, lang: String, position: Int): Flow<PhraseEntity?>

	@Query("SELECT * FROM phrase_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<PhraseEntity?>

	@Query("SELECT EXISTS (SELECT * FROM phrase_table WHERE phrase = :phrase)")
	suspend fun exists(phrase: String): Boolean

	@Query("SELECT id FROM phrase_table WHERE phrase LIKE '%' || :like || '%' AND lang = :lang")
	fun getListIdFlow(like: String, lang: String): Flow<List<Int>>

	@Query("SELECT id FROM phrase_table WHERE phrase LIKE '%' || :like || '%'")
	fun getListIdFlow(like: String): Flow<List<Int>>


}