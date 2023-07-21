package com.uogames.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.uogames.database.entity.ModuleEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ModuleDAO {

	@Insert
	suspend fun insert(module: ModuleEntity): Long

	@Update
	suspend fun update(module: ModuleEntity): Int

	@Delete
	suspend fun delete(module: ModuleEntity): Int

	@Query("SELECT COUNT(id) FROM modules")
	suspend fun count(): Int

	@Query("SELECT COUNT(id) FROM modules WHERE name LIKE  '%' ||:text || '%' ")
	suspend fun count(text: String): Int

	@RawQuery
	suspend fun count(query: SupportSQLiteQuery): Int

	@RawQuery
	suspend fun get(query: SupportSQLiteQuery): ModuleEntity?

	@Query("SELECT DISTINCT COUNT(m.id) FROM modules AS m \n" +
			"LEFT JOIN  module_card AS mc \n" +
			"ON m.id = mc.id_module \n" +
			"LEFT JOIN cards_table AS ct \n" +
			"ON ct.id = mc.id_card \n" +
			"LEFT JOIN phrase_table AS pt1 \n" +
			"ON pt1.id = ct.id_phrase \n" +
			"LEFT JOIN phrase_table AS pt2 \n" +
			"ON pt2.id = ct.id_translate \n" +
			"WHERE m.name LIKE  '%' ||:text || '%' " +
			"AND pt1.lang = :fLang " +
			"AND pt2.lang = :sLang " +
			"AND pt1.country = :fCountry " +
			"AND pt2.country = :sCountry ")
	suspend fun countLong(
		text: String,
		fLang: String,
		sLang: String,
		fCountry: String,
		sCountry: String
	): Int

	@Query("SELECT * FROM modules LIMIT :position, 1")
	suspend fun get(position: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%'  LIMIT :position, 1")
	suspend fun get(like: String, position: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE id = :id")
	suspend fun getById(id: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE global_id = :globalId")
	suspend fun getByGlobalId(globalId: String): ModuleEntity?

	@Query("SELECT COUNT(id) FROM modules")
	fun getCountFlow(): Flow<Int>

	@Query("SELECT COUNT(id) FROM modules WHERE name LIKE  '%' ||:like || '%' ")
	fun getCountLike(like: String): Flow<Int>

	@Query("SELECT * FROM modules")
	fun getList(): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%' ")
	fun getListLike(like: String): Flow<List<ModuleEntity>>

	@Query("SELECT * FROM modules WHERE name LIKE  '%' ||:like || '%'  LIMIT :position, 1")
	suspend fun getByPosition(like: String, position: Int): ModuleEntity?

	@Query("SELECT * FROM modules WHERE id = :id")
	fun getByIdFlow(id: Int): Flow<ModuleEntity?>

	@Query("SELECT changed FROM modules WHERE id = :id")
	fun isChanged(id: Int): Flow<Boolean?>

}