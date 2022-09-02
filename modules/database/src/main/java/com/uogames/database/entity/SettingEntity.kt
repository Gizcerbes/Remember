package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
	@PrimaryKey(autoGenerate = false)
	@ColumnInfo(name = "key")
	val key: String,
	@ColumnInfo(name = "value")
	val value: String?
){

	companion object{
		private const val v1 = "CREATE TABLE `settings` (" +
				"`key` TEXT NOT NULL, " +
				"`value` TEXT, " +
				"PRIMARY KEY(`key`)" +
				");"
	}

}