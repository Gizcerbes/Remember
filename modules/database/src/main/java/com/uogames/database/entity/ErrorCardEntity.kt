package com.uogames.database.entity

import androidx.room.*


@Entity(
	tableName = "error_card",
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
		)],
	indices = [Index("idPhrase", "idTranslate", unique = true)]
)
data class ErrorCardEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val idPhrase: Int,
	@ColumnInfo(index = true)
	val idTranslate: Int,
	val correct: Long,
	val incorrect: Long,
	val percentCorrect: Byte
)