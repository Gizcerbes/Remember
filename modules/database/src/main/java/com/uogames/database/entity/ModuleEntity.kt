package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "modules",
)
data class ModuleEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "name")
	val name: String,
	@ColumnInfo(name = "owner")
	val owner: String,
	@ColumnInfo(name = "time_change")
	val timeChange: Long,
	@ColumnInfo(name="like")
	val like: Long,
	@ColumnInfo(name = "dislike")
	val dislike: Long,
	@ColumnInfo(name = "global_id")
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)