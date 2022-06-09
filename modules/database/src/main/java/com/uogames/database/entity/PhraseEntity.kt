package com.uogames.database.entity

import androidx.room.*

@Entity(
	tableName = "phrase_table",
)
data class PhraseEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val phrase: String,
	val definition: String?,
	val lang: String?,
	val idPronounce: Int?,
	val idImage: Int?,
	val timeChange: Long
)