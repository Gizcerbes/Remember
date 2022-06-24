package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
	@PrimaryKey(autoGenerate = false)
	val key: String,
	val value: String?
)