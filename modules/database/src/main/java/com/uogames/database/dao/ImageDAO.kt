package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ImageEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

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

	@Query("SELECT * FROM images_table WHERE global_id = :id")
	suspend fun getByGlobalId(id: UUID): ImageEntity?

	@Query("SELECT * FROM images_table WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<ImageEntity?>

	@Query(
		"SELECT it.* FROM images_table AS it " +
				"LEFT JOIN  phrase_table AS pt " +
				"ON pt.id_image = it.id " +
				"LEFT JOIN cards_table AS ct " +
				"ON ct.id_image = it.id " +
				"WHERE pt.id_image IS NULL " +
				"AND ct.id_image IS NULL"
	)
	suspend fun freeImages(): List<ImageEntity>

	@Query("SELECT * FROM images_table ORDER BY id DESC")
	suspend fun getList(): List<ImageEntity>

	@Query("SELECT * FROM images_table ORDER BY id DESC")
	fun getListFlow(): Flow<List<ImageEntity>>

}