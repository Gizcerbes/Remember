package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.CardEntity

@Dao
interface CardDAO {

    @Insert
    suspend fun insert(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    @Update
    suspend fun update(card: CardEntity)

    @Query("SELECT COUNT(id) FROM card_table")
    suspend fun count(): Long

    @Query("SELECT * FROM card_table LIMIT :number , 1 ")
    suspend fun get(number: Long): CardEntity

}