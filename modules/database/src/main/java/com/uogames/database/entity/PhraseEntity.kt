package com.uogames.database.entity

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Entity(
	tableName = "phrase_table"
)
data class PhraseEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "phrase")
	val phrase: String,
	@ColumnInfo(name = "definition")
	val definition: String?,
	@ColumnInfo(name = "lang")
	val lang: String,
	@ColumnInfo(name = "country")
	val country: String,
	@ColumnInfo(name = "id_pronounce")
	val idPronounce: Int?,
	@ColumnInfo(name = "id_image")
	val idImage: Int?,
	@ColumnInfo(name = "time_change")
	val timeChange: Long,
	@ColumnInfo(name = "like")
	val like: Long,
	@ColumnInfo(name = "dislike")
	val dislike: Long,
	@ColumnInfo(name = "global_id")
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?,
	@ColumnInfo(name = "changed", defaultValue = "false")
	val changed: Boolean = false
) {

	companion object {
		private const val v1 = "CREATE TABLE " +
				"`phrase_table` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`phrase` TEXT NOT NULL, " +
				"`definition` TEXT, " +
				"`lang` TEXT NOT NULL, " +
				"`id_pronounce` INTEGER, " +
				"`id_image` INTEGER, " +
				"`time_change` INTEGER NOT NULL, " +
				"`like` INTEGER NOT NULL, " +
				"`dislike` INTEGER NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT" +
				");"

		private const val v3 = "CREATE TABLE " +
				"`phrase_table` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`phrase` TEXT NOT NULL, " +
				"`definition` TEXT, " +
				"`lang` TEXT NOT NULL, " +
				"`country` TEXT NOT NULL, " +
				"`id_pronounce` INTEGER, " +
				"`id_image` INTEGER, " +
				"`time_change` INTEGER NOT NULL, " +
				"`like` INTEGER NOT NULL, " +
				"`dislike` INTEGER NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT" +
				");"


		fun migration_2_3(): Migration {
			return object: Migration(2,3){
				override fun migrate(database: SupportSQLiteDatabase) {
					database.execSQL("ALTER TABLE `phrase_table` ADD `country` TEXT NOT NULL DEFAULT 'UNITED_KINGDOM'")
					val result = database.query("SELECT * FROM `phrase_table`")
					if (result.moveToFirst()) do {
						val lan = result.getString(result.getColumnIndexOrThrow("lang")).split("-")
						database.execSQL(
							"UPDATE `phrase_table` " +
									"SET `lang` = '${lan[0]}', " +
									"`country` = '${lan[1]}' " +
									"WHERE `id` = ${result.getInt(result.getColumnIndexOrThrow("id"))}"
						)
					} while (result.moveToNext())
				}
			}
		}
	}


}
