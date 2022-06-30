package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "modules",
)
data class ModuleEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val name: String,
)