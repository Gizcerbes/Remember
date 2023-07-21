package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_table")
data class CacheEntity(
	@ColumnInfo(name = "id")
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val data: String
)