package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.uogames.database.entity.TestEntity
import java.util.*

@Dao
interface TestDAO {

	@Insert
	suspend fun insert(testEntity: TestEntity): Long

	@Query("SELECT * FROM test WHERE id = :id")
	suspend fun getById(id: Long): TestEntity?


}