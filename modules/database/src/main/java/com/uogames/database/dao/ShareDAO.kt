package com.uogames.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uogames.database.entity.ShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShareDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shareEntity: ShareEntity): Long

    @Delete
    suspend fun delete(shareEntity: ShareEntity)

    @Query("SELECT * FROM share_table LIMIT 1")
    suspend fun getFirst(): ShareEntity?

    @Query("SELECT COUNT(id) FROM share_table")
    suspend fun count(): Int

    @Query("SELECT COUNT(id) FROM share_table")
    fun countFlow(): Flow<Int>

    @Query("SELECT EXISTS " +
            "(SELECT id FROM share_table " +
            "WHERE id = :id " +
            "OR id_image = :idImage " +
            "OR id_pronounce = :idPronounce " +
            "OR id_phrase = :idPhrase " +
            "OR id_card = :idCard " +
            "OR id_module = :idModule " +
            "OR id_module_card = :idModuleCard) ")
    suspend fun exists(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ): Boolean

    @Query("SELECT EXISTS " +
            "(SELECT id FROM share_table " +
            "WHERE id = :id " +
            "OR id_image = :idImage " +
            "OR id_pronounce = :idPronounce " +
            "OR id_phrase = :idPhrase " +
            "OR id_card = :idCard " +
            "OR id_module = :idModule " +
            "OR id_module_card = :idModuleCard) ")
    fun existsFlow(
        id: Int = -1,
        idImage: Int = -1,
        idPronounce: Int = -1,
        idPhrase: Int = -1,
        idCard: Int = -1,
        idModule: Int = -1,
        idModuleCard: Int = -1
    ): Flow<Boolean>

    @Query("DELETE FROM share_table")
    suspend fun clean()

}