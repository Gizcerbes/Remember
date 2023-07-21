package com.uogames.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.ErrorCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ErrorCardDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(errorCardEntity: ErrorCardEntity): Long

	@Update
	suspend fun update(errorCardEntity: ErrorCardEntity): Int

	@Delete
	suspend fun delete(errorCardEntity: ErrorCardEntity): Int

	@Query("SELECT COUNT(id) FROM error_card WHERE id_phrase = :idPhrase OR id_translate = :idPhrase")
	suspend fun countByPhraseId(idPhrase: Int): Long

	@Query("SELECT * FROM error_card WHERE id_phrase = :idPhrase OR id_translate = :idPhrase LIMIT :number, 1")
	suspend fun getByPhraseId(idPhrase: Int, number: Long): ErrorCardEntity?

	@Query("SELECT * FROM error_card " +
			"WHERE (id_phrase =:idPhrase AND id_translate=:idTranslate) " +
			"OR (id_phrase = :idTranslate AND id_translate = :idPhrase)")
	suspend fun getByPhraseAndTranslate(idPhrase: Int, idTranslate: Int): ErrorCardEntity?


	@RawQuery
	suspend fun count(query: SupportSQLiteQuery): Int

	@RawQuery(observedEntities = [ErrorCardEntity::class])
	fun countFlow(query: SupportSQLiteQuery): Flow<Int>

	@RawQuery
	suspend fun get(query: SupportSQLiteQuery): ErrorCardEntity?

	@RawQuery(observedEntities = [ErrorCardEntity::class])
	fun getFlow(query: SupportSQLiteQuery): Flow<ErrorCardEntity?>

}