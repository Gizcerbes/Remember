package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun save(setting: SettingEntity)

	@Delete
	suspend fun delete(setting: SettingEntity): Int

	@Query("SELECT * FROM settings WHERE `key`=:id")
	suspend fun get(id: String): SettingEntity?

	@Query("SELECT * FROM settings WHERE `key`=:key")
	fun getFlow(key: String): Flow<SettingEntity?>

}