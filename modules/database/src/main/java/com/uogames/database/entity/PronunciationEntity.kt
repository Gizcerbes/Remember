package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pronounce_table")
data class PronunciationEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "audio_uri")
	val audioUri: String,
	@ColumnInfo(name = "global_id")
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)