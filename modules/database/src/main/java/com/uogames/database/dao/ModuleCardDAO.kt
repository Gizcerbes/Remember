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

	@Query("SELECT * FROM module_card ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomModuleCard():ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE idModule  =:idModule ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomModuleCard(idModule: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE idCard <> :idCard ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithout(idCard: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE idModule = :idModule AND id <> :idCard ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithout(idModule: Int, idCard: Int): ModuleCardEntity?
}