package com.uogames.database.entity

import androidx.room.*
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
	val globalOwner: String?
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
	}

}
