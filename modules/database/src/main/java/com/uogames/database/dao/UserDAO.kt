package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.uogames.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

	@Insert(onConflict = REPLACE)
	suspend fun insert(userEntity: UserEntity): Long

	@Delete
	suspend fun delete(userEntity: UserEntity): Int

	@Query("SELECT * FROM users_table WHERE global_id = :id")
	suspend fun getById(id: String): UserEntity?

	@Query("SELECT * FROM users_table WHERE global_id = :id")
	fun getByIdFlow(id:String): Flow<UserEntity?>

}