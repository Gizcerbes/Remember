package com.uogames.database.entity

import androidx.room.*
import androidx.room.migration.Migration
import androidx.room.util.convertByteToUUID
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Entity(
	tableName = "cards_table",
	foreignKeys = [
		ForeignKey(
			entity = PhraseEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_phrase"],
			onDelete = ForeignKey.CASCADE
		), ForeignKey(
			entity = PhraseEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_translate"],
			onDelete = ForeignKey.CASCADE
		)],
	indices = [
		Index("id_phrase", "id_translate", unique = true),
		Index(value = ["global_id"], orders = [Index.Order.ASC])
	]
)

data class CardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "id_phrase")
	val idPhrase: Int,
	@ColumnInfo(name = "id_translate", index = true)
	val idTranslate: Int,
	@ColumnInfo(name = "id_image")
	val idImage: Int?,
	@ColumnInfo(name = "reason")
	val reason: String,
	@ColumnInfo(name = "time_change")
	val timeChange: Long,
	@ColumnInfo(name = "like")
	val like: Long,
	@ColumnInfo(name = "dislike")
	val dislike: Long,
	@ColumnInfo(name = "global_id")
	val globalId: String,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?,
	@ColumnInfo(name = "changed", defaultValue = "false")
	val changed: Boolean = false
)