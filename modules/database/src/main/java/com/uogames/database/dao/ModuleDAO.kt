package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDAO {

	@Insert
	suspend fun insert(module: ModuleEntity): Long

	@Update
	suspend fun update(module: ModuleEntity): Int

	@Delete
	suspend fun delete(module: ModuleEntity): Int

	@Query("SELECT COUNT(id) FROM modules")
	fun getCount(): Flow<Int>

	@Query("SELECT * FROM modules")
	fun getList(): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%' ")
	fun getListLike(like: String): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE id = :id")
	fun getByID(id: Int): Flow<ModuleEntity?>

}