package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	tableName = "new_cards_table", foreignKeys = [
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
	val idPhrase: Int,
	val idTranslate: Int,
	val idImage: Int?,
	val reason: String
)