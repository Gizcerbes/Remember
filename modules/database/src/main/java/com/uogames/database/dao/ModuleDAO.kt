package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ModuleEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

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

	@Query("SELECT COUNT(id) FROM modules WHERE name LIKE  '%' ||:like || '%' ")
	fun getCountLike(like: String): Flow<Int>

	@Query("SELECT * FROM modules")
	fun getList(): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%' ")
	fun getListLike(like: String): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%'  LIMIT :position, 1")
	suspend fun getByPosition(like: String, position: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE id = :id")
	suspend fun getById(id: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE global_id = :globalId")
	suspend fun getByGlobalId(globalId: UUID): ModuleEntity?

	@Query("SELECT * FROM modules WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<ModuleEntity?>

}