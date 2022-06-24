package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pronounce_table")
data class PronunciationEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val audioUri: String
)