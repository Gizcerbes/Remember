package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(userEntity: UserEntity): Long

	@Delete
	suspend fun delete(userEntity: UserEntity): Int

	@Query("SELECT * FROM users_table WHERE global_id = :id")
	suspend fun getById(id: String): UserEntity?

	@Query("SELECT * FROM users_table WHERE global_id = :id")
	fun getByIdFlow(id: String): Flow<UserEntity?>

	@RawQuery
	suspend fun getEntity(query: SupportSQLiteQuery): UserEntity?

	@RawQuery(observedEntities = [UserEntity::class])
	fun getEntityFlow(query: SupportSQLiteQuery): Flow<UserEntity?>

}