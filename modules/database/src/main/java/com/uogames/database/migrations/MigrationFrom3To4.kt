package com.uogames.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationFrom3To4 : Migration(3, 4) {
	override fun migrate(database: SupportSQLiteDatabase) {
		val v4 =
			"CREATE TABLE `share_table` " +
					"(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`id_image` INTEGER, " +
					"`id_pronounce` INTEGER, " +
					"`id_phrase` INTEGER, " +
					"`id_card` INTEGER, " +
					"`id_module` INTEGER, " +
					"`id_module_card` INTEGER, " +
					"FOREIGN KEY(`id_image`) REFERENCES `images_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_pronounce`) REFERENCES `pronounce_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_phrase`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_card`) REFERENCES `cards_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_module`) REFERENCES `modules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_module_card`) REFERENCES `module_card`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );"

		database.execSQL(v4)
	}
}
