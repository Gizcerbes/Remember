package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words_table")
data class WordEntity(
	@PrimaryKey(autoGenerate = false)
	val word: String
)