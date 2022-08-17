package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
		)]
)

data class CardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "id_phrase", index = true)
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
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)