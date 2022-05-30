package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	tableName = "new_cards_table", foreignKeys = arrayOf(
		ForeignKey(
			entity = WordEntity::class,
			parentColumns = arrayOf("word"),
			childColumns = arrayOf("word"),
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = WordEntity::class,
			parentColumns = arrayOf("word"),
			childColumns = arrayOf("translate"),
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = ImageEntity::class,
			parentColumns = arrayOf("id"),
			childColumns = arrayOf("imgBase64"),
			onDelete = ForeignKey.CASCADE
		)
	)
)
data class NewCardEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val word: String,
	val translate: String,
	val langFrom: String,
	val langTo: String,
	val imgBase64: Int?
)