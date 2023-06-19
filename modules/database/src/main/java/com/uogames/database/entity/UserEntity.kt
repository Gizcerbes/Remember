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
) {

	companion object {

		private const val v2 = "CREATE TABLE `users_table` (" +
				"`global_id` TEXT NOT NULL, " +
				"`name` TEXT NOT NULL, " +
				"PRIMARY KEY(`global_id`)" +
				");"

		fun migration_1_2(): Migration {
			return object : Migration(1, 2) {
				override fun migrate(database: SupportSQLiteDatabase) {
					database.execSQL(v2)
				}

			}
		}


	}

}