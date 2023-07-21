package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uogames.database.entity.CacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: List<CacheEntity>)

	@Query("DELETE FROM cache_table")
	suspend fun clean()

	@Query("SELECT * FROM cache_table LIMIT 1 OFFSET :position")
	suspend fun get(position: Int): CacheEntity?

	@Query("SELECT * FROM cache_table WHERE id = :id")
	suspend fun getById(id:Int): CacheEntity?

	@Query("SELECT COUNT(id) FROM cache_table")
	suspend fun count(): Int

	@Query("SELECT COUNT(id) FROM cache_table")
	fun countFlow(): Flow<Int>

	@Query("SELECT * FROM cache_table")
	fun getAll(): Flow<List<CacheEntity>>

}