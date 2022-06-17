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
	fun count(): Flow<Int>

	@Query("SELECT * FROM images_table WHERE id = :id")
	fun getByID(id: Int): Flow<ImageEntity?>

	@Query(
		"DELETE FROM images_table " +
				"WHERE " +
				"NOT EXISTS (SELECT * FROM phrase_table pt WHERE pt.idImage = images_table.id ) " +
				"AND " +
				"NOT EXISTS (SELECT * FROM new_cards_table nct WHERE nct.idImgBase64 = images_table.id)"
	)
	suspend fun clean()

}