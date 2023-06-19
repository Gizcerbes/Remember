package com.uogames.database.dao

import androidx.room.*
import com.uogames.database.entity.ModuleCardEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ModuleCardDAO {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(mcEntity: ModuleCardEntity): Long

	@Update
	suspend fun update(mcEntity: ModuleCardEntity): Int

	@Delete
	suspend fun delete(mcEntity: ModuleCardEntity): Int

	@Query("SELECT * FROM module_card WHERE id_module = :id")
	fun getByModuleID(id: Int): Flow<List<ModuleCardEntity>>

	@Query("SELECT COUNT(id) FROM module_card WHERE id_module = :id")
	fun getCountByModuleIdFlow(id: Int): Flow<Int>

	@Query("SELECT COUNT(id) FROM module_card WHERE id_module = :id")
	suspend fun getCountByModuleId(id: Int): Int

	@Query("SELECT * FROM module_card WHERE id_module = :idModule LIMIT :position, 1")
	suspend fun getByPositionOfModule(idModule: Int, position: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomModuleCard(): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE id_module = :idModule ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomModuleCard(idModule: Int): ModuleCardEntity?

	@Query("SELECT mc.* FROM module_card AS mc " +
			"JOIN cards_table AS ct ON mc.id_card = ct.id " +
			"LEFT JOIN error_card AS ec ON ct.id_phrase = ec.id_phrase AND ct.id_translate = ec.id_translate " +
			"WHERE mc.id_module = :idModule " +
			"ORDER BY ec.percent_correct ASC " +
			"LIMIT 1")
	suspend fun getUnknowable(idModule: Int): ModuleCardEntity?

	@Query("SELECT mc.* FROM module_card AS mc " +
			"JOIN cards_table AS ct ON mc.id_card = ct.id " +
			"LEFT JOIN error_card AS ec ON  ct.id_translate = ec.id_translate " +
			"WHERE mc.id_module = :idModule " +
			"AND ec.id_phrase = :idPhrase " +
			"ORDER BY ec.percent_correct ASC " +
			"LIMIT 1")
	suspend fun getConfusing(idModule: Int, idPhrase: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE id_card <> :idCard ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithout(idCard: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE id_module = :idModule AND id <> :idCard ORDER BY RANDOM() LIMIT 1")
	suspend fun getRandomWithout(idModule: Int, idCard: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE id = :moduleCardId")
	suspend fun getById(moduleCardId: Int): ModuleCardEntity?

	@Query("SELECT * FROM module_card WHERE global_id = :globalId")
	suspend fun getByGlobalId(globalId: UUID): ModuleCardEntity?

	@Query("DELETE FROM module_card WHERE id_module = :idModule")
	suspend fun removeByModule(idModule: Int)

}