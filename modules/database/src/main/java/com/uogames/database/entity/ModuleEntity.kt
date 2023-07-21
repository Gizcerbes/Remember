package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
	tableName = "modules",
	indices = [
		Index(value = ["global_id"], orders = [Index.Order.ASC])
	]
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
	val globalId: String,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?,
	@ColumnInfo(name = "changed", defaultValue = "false")
	val changed: Boolean = false
)