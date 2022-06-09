package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pronounce_table")
data class PronunciationEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	@ColumnInfo(typeAffinity = ColumnInfo.TEXT)
	val dataBase64: String
)