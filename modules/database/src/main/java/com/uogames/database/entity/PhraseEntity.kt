package com.uogames.database.entity

import androidx.room.*

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
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)