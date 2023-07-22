package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDAO {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(downloadEntity: DownloadEntity): Long

	@Delete
	suspend fun delete(downloadEntity: DownloadEntity)

	@Query("SELECT COUNT(id) FROM download_table")
	suspend fun count(): Int

	@Query("SELECT COUNT(id) FROM download_table")
	fun countFlow(): Flow<Int>

	@Query("SELECT * FROM download_table LIMIT 1")
	suspend fun getFirst(): DownloadEntity?

	@Query("DELETE FROM download_table")
	suspend fun clean()

	@RawQuery(observedEntities = [DownloadEntity::class])
	fun existsFlow(query: SupportSQLiteQuery): Flow<Boolean>

	@RawQuery
	suspend fun exists(query: SupportSQLiteQuery): Boolean

}