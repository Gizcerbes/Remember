package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {

    @Insert
    suspend fun insert(card: CardEntity)

	@Insert
	suspend fun insert(vararg card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    @Update
    suspend fun update(card: CardEntity)

	@Query("SELECT COUNT(id) FROM card_table WHERE phrase LIKE '%' || :nameItems || '%' OR translate LIKE '%' || :nameItems || '%'")
	fun countFLOW(nameItems: String): Flow<Int>

	@Query("SELECT * FROM card_table WHERE phrase LIKE '%' || :nameItems || '%' OR translate LIKE '%' || :nameItems || '%' LIMIT :number, 1")
	fun getCardFLOW(number: Int, nameItems: String): Flow<CardEntity?>

    @Query("SELECT * FROM card_table ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): CardEntity?

    @Query("SELECT * FROM card_table WHERE phrase != :phrase OR translate != :translate ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWithout(phrase: String, translate: String): CardEntity?

    @Query("SELECT EXISTS (SELECT * FROM card_table WHERE phrase = :phrase AND translate = :translate)")
    suspend fun exists(phrase: String, translate: String): Boolean

}