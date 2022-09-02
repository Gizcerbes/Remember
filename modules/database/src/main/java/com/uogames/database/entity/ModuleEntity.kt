package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

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
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
){
	companion object{
		private const val v1 = "CREATE TABLE `modules` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`name` TEXT NOT NULL, " +
				"`owner` TEXT NOT NULL, " +
				"`time_change` INTEGER NOT NULL, " +
				"`like` INTEGER NOT NULL, " +
				"`dislike` INTEGER NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT" +
				");"
	}
}