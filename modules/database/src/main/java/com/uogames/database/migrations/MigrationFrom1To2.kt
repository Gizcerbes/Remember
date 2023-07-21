package com.uogames.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationFrom1To2: Migration(1,2) {
	override fun migrate(database: SupportSQLiteDatabase) {
		val v2 = "CREATE TABLE `users_table` (" +
				"`global_id` TEXT NOT NULL, " +
				"`name` TEXT NOT NULL, " +
				"PRIMARY KEY(`global_id`)" +
				");"
		database.execSQL(v2)
	}
}