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
	val owner: String,
	val timeChange: Long,
	val like: Long,
	val dislike: Long,
	val globalId: Long?,
	val globalOwner: String?
)