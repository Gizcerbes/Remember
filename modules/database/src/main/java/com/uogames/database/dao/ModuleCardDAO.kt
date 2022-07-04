package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ModuleCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleCardDAO {

	@Insert
	suspend fun insert(mcEntity: ModuleCardEntity): Long

	@Update
	suspend fun update(mcEntity: ModuleCardEntity): Int

	@Delete
	suspend fun delete(mcEntity: ModuleCardEntity): Int

	@Query("SELECT * FROM module_card WHERE idModule = :id")
	fun getByModuleID(id: Int): Flow<List<ModuleCardEntity>>

	@Query("SELECT COUNT(id) FROM module_card WHERE idModule = :id")
	fun getCountByModuleID(id: Int): Flow<Int>

}