package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDAO {

	@Insert
	suspend fun insert(imageEntity: ImageEntity): Long

	@Delete
	suspend fun delete(imageEntity: ImageEntity): Int

	@Update
	suspend fun update(imageEntity: ImageEntity): Int

	@Query("SELECT * FROM images_table WHERE id = :id")
	fun getByID(id: Int): Flow<ImageEntity?>


}