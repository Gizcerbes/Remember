package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "users_table")
data class UserEntity(
	@PrimaryKey(autoGenerate = false)
	@ColumnInfo(name = "global_id")
	val globalId: String,
	@ColumnInfo(name = "name")
	val name: String
)