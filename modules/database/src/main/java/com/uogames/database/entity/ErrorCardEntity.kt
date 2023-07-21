package com.uogames.database.entity

import androidx.room.*


@Entity(
	tableName = "error_card",
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
	indices = [Index("id_phrase", "id_translate", unique = true)]
)
data class ErrorCardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Long = 0,
	@ColumnInfo(name = "id_phrase", index = true)
	val idPhrase: Int,
	@ColumnInfo(name = "id_translate", index = true)
	val idTranslate: Int,
	@ColumnInfo(name = "correct")
	val correct: Long,
	@ColumnInfo(name = "incorrect")
	val incorrect: Long,
	@ColumnInfo(name = "percent_correct")
	val percentCorrect: Byte
)