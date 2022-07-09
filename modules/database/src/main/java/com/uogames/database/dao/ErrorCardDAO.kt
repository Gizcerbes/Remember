package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ErrorCardEntity

@Dao
interface ErrorCardDAO {

	@Insert
	suspend fun insert(errorCardEntity: ErrorCardEntity): Long

	@Update
	suspend fun update(errorCardEntity: ErrorCardEntity): Int

	@Delete
	suspend fun delete(errorCardEntity: ErrorCardEntity): Int

	@Query("SELECT COUNT(id) FROM error_card WHERE idPhrase = :idPhrase OR idTranslate = :idPhrase")
	suspend fun countByPhraseId(idPhrase: Int): Long

	@Query("SELECT * FROM error_card WHERE idPhrase = :idPhrase OR idTranslate = :idPhrase LIMIT :number, 1")
	suspend fun getByPhraseId(idPhrase: Int, number: Long): ErrorCardEntity?

	@Query("SELECT * FROM error_card " +
			"WHERE (idPhrase =:idPhrase AND idTranslate=:idTranslate) " +
			"OR (idPhrase = :idTranslate AND idTranslate = :idPhrase)")
	suspend fun getByPhraseAndTranslate(idPhrase: Int, idTranslate: Int): ErrorCardEntity?


}