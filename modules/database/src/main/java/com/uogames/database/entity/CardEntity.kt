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
			childColumns = ["idPhrase"],
			onDelete = ForeignKey.CASCADE
		), ForeignKey(
			entity = PhraseEntity::class,
			parentColumns = ["id"],
			childColumns = ["idTranslate"],
			onDelete = ForeignKey.CASCADE
		)]
)

data class CardEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	@ColumnInfo(index = true)
	val idPhrase: Int,
	@ColumnInfo(index = true)
	val idTranslate: Int,
	val idImage: Int?,
	val reason: String,
	val timeChange: Long,
	val like: Long,
	val dislike: Long,
	val globalId: Long?,
	val globalOwner: String?
)