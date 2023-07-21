package com.uogames.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationFrom2To3: Migration(2,3) {
	override fun migrate(database: SupportSQLiteDatabase) {
		database.execSQL("ALTER TABLE `phrase_table` ADD `country` TEXT NOT NULL DEFAULT 'UNITED_KINGDOM'")
		val result = database.query("SELECT * FROM `phrase_table`")
		if (result.moveToFirst()) do {
			val lan = result.getString(result.getColumnIndexOrThrow("lang")).split("-")
			database.execSQL(
				"UPDATE `phrase_table` " +
						"SET `lang` = '${lan[0]}', " +
						"`country` = '${lan[1]}' " +
						"WHERE `id` = ${result.getInt(result.getColumnIndexOrThrow("id"))}"
			)
		} while (result.moveToNext())
	}
}