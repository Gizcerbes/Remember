package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(wordEntity: WordEntity)

	@Delete
	suspend fun delete(wordEntity: WordEntity)

	@Query("SELECT COUNT(word) FROM words_table")
	fun countFLOW(): Flow<Int>

	@Query("SELECT * FROM words_table LIMIT :number , 1 ")
	fun getByNumberFLOW(number: Int): Flow<WordEntity?>

	@Query("SELECT EXISTS (SELECT * FROM words_table WHERE word = :word)")
	suspend fun exists(word: WordEntity): Boolean


}