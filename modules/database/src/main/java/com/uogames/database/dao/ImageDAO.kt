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

	@Query("SELECT COUNT(*) FROM images_table")
	suspend fun count(): Int

	@Query("SELECT COUNT(*) FROM images_table")
	fun countFlow(): Flow<Int>

	@Query("SELECT * FROM images_table WHERE id = :id")
	suspend fun getById(id: Int): ImageEntity?

	@Query("SELECT * FROM images_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<ImageEntity?>

	@Query(
		"SELECT * FROM images_table " +
				"WHERE " +
				"NOT EXISTS (SELECT * FROM phrase_table pt WHERE pt.idImage = images_table.id ) " +
				"AND " +
				"NOT EXISTS (SELECT * FROM cards_table nct WHERE nct.idImage = images_table.id)"
	)
	suspend fun freeImages(): List<ImageEntity>

	@Query("SELECT * FROM images_table")
	suspend fun getList(): List<ImageEntity>

	@Query("SELECT * FROM images_table")
	fun getListFlow(): Flow<List<ImageEntity>>

}