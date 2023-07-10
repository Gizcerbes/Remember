package com.uogames.database.migrations

import androidx.core.database.getBlobOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.nio.ByteBuffer
import java.util.UUID

object MigrationFrom6To7 : Migration(6, 7) {


	override fun migrate(database: SupportSQLiteDatabase) {
		migrationPronounce(database)
		migrationImage(database)
		migrationPhrases(database)
		migrationCard(database)
		migrationModule(database)
		migrationModuleCard(database)
		migrationDownload(database)
		addIndexes(database)
	}

	private fun migrationPronounce(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `pronounce_table_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`audio_uri` TEXT NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT" +
					");"
		)
		val result = database.query("SELECT * FROM `pronounce_table`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `pronounce_table_1` VALUES (?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getString(result.getColumnIndexOrThrow("audio_uri")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner")),
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `pronounce_table`;")
		database.execSQL("ALTER TABLE `pronounce_table_1` RENAME TO `pronounce_table`;")
	}

	private fun migrationImage(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `images_table_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`img_uri` TEXT NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT" +
					");"
		)

		val result = database.query("SELECT * FROM `images_table`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `images_table_1` VALUES (?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getString(result.getColumnIndexOrThrow("img_uri")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner")),
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `images_table`;")
		database.execSQL("ALTER TABLE `images_table_1` RENAME TO `images_table`;")
	}

	private fun migrationPhrases(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `phrase_table_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`phrase` TEXT NOT NULL, " +
					"`definition` TEXT, " +
					"`lang` TEXT NOT NULL, " +
					"`country` TEXT NOT NULL, " +
					"`id_pronounce` INTEGER, " +
					"`id_image` INTEGER, " +
					"`time_change` INTEGER NOT NULL, " +
					"`like` INTEGER NOT NULL, " +
					"`dislike` INTEGER NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT, " +
					"`changed` INTEGER NOT NULL DEFAULT false, " +
					"FOREIGN KEY(`id_pronounce`) REFERENCES `pronounce_table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , " +
					"FOREIGN KEY(`id_image`) REFERENCES `images_table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL );"
		)

		val result = database.query("SELECT * FROM `phrase_table`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `phrase_table_1` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getString(result.getColumnIndexOrThrow("phrase")),
					result.getString(result.getColumnIndexOrThrow("definition")),
					result.getString(result.getColumnIndexOrThrow("lang")),
					result.getString(result.getColumnIndexOrThrow("country")),
					result.getIntOrNull(result.getColumnIndexOrThrow("id_pronounce")),
					result.getIntOrNull(result.getColumnIndexOrThrow("id_image")),
					result.getInt(result.getColumnIndexOrThrow("time_change")),
					result.getInt(result.getColumnIndexOrThrow("like")),
					result.getInt(result.getColumnIndexOrThrow("dislike")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner")),
					result.getInt(result.getColumnIndexOrThrow("changed"))
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `phrase_table`;")
		database.execSQL("ALTER TABLE `phrase_table_1` RENAME TO `phrase_table`;")
	}

	private fun migrationCard(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `cards_table_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`id_phrase` INTEGER NOT NULL, " +
					"`id_translate` INTEGER NOT NULL, " +
					"`id_image` INTEGER, " +
					"`reason` TEXT NOT NULL, " +
					"`time_change` INTEGER NOT NULL, " +
					"`like` INTEGER NOT NULL, " +
					"`dislike` INTEGER NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT, " +
					"`changed` INTEGER NOT NULL DEFAULT false, " +
					"FOREIGN KEY(`id_phrase`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_translate`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );"
		)

		val result = database.query("SELECT * FROM `cards_table`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `cards_table_1` VALUES (?,?,?,?,?,?,?,?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getInt(result.getColumnIndexOrThrow("id_phrase")),
					result.getInt(result.getColumnIndexOrThrow("id_translate")),
					result.getIntOrNull(result.getColumnIndexOrThrow("id_image")),
					result.getString(result.getColumnIndexOrThrow("reason")),
					result.getInt(result.getColumnIndexOrThrow("time_change")),
					result.getInt(result.getColumnIndexOrThrow("like")),
					result.getInt(result.getColumnIndexOrThrow("dislike")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner")),
					result.getInt(result.getColumnIndexOrThrow("changed"))
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `cards_table`;")
		database.execSQL("ALTER TABLE `cards_table_1` RENAME TO `cards_table`;")
	}

	private fun migrationModule(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `modules_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`name` TEXT NOT NULL, " +
					"`owner` TEXT NOT NULL, " +
					"`time_change` INTEGER NOT NULL, " +
					"`like` INTEGER NOT NULL, " +
					"`dislike` INTEGER NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT, " +
					"`changed` INTEGER NOT NULL DEFAULT false" +
					");"
		)

		val result = database.query("SELECT * FROM `modules`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `modules_1` VALUES (?,?,?,?,?,?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getString(result.getColumnIndexOrThrow("name")),
					result.getString(result.getColumnIndexOrThrow("owner")),
					result.getInt(result.getColumnIndexOrThrow("time_change")),
					result.getInt(result.getColumnIndexOrThrow("like")),
					result.getInt(result.getColumnIndexOrThrow("dislike")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner")),
					result.getInt(result.getColumnIndexOrThrow("changed"))
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `modules`;")
		database.execSQL("ALTER TABLE `modules_1` RENAME TO `modules`;")
	}

	private fun migrationModuleCard(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `module_card_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`id_module` INTEGER NOT NULL, " +
					"`id_card` INTEGER NOT NULL, " +
					"`global_id` TEXT NOT NULL, " +
					"`global_owner` TEXT, " +
					"FOREIGN KEY(`id_module`) REFERENCES `modules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`id_card`) REFERENCES `cards_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );"
		)

		val result = database.query("SELECT * FROM `module_card`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `module_card_1` VALUES (?,?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getInt(result.getColumnIndexOrThrow("id_module")),
					result.getInt(result.getColumnIndexOrThrow("id_card")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getStringOrNull(result.getColumnIndexOrThrow("global_owner"))
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `module_card`;")
		database.execSQL("ALTER TABLE `module_card_1` RENAME TO `module_card`;")
	}

	private fun migrationDownload(database: SupportSQLiteDatabase) {
		database.execSQL(
			"CREATE TABLE `download_table_1` (" +
					"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					"`global_phrase_id` TEXT, " +
					"`global_card_id` TEXT, " +
					"`global_module_id` TEXT" +
					");"
		)

		val result = database.query("SELECT * FROM `download_table`")
		if (result.moveToFirst()) do {
			database.execSQL(
				"INSERT INTO `download_table_1` VALUES (?,?,?,?)",
				arrayOf(
					result.getInt(result.getColumnIndexOrThrow("id")),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_phrase_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_card_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString(),
					result.getBlobOrNull(result.getColumnIndexOrThrow("global_module_id"))?.let { convertByteToUUID(it) }?.toString()
						?: UUID.randomUUID().toString()
				)
			)
		} while (result.moveToNext())

		database.execSQL("DROP TABLE `download_table`;")
		database.execSQL("ALTER TABLE `download_table_1` RENAME TO `download_table`;")

	}

	private fun addIndexes(database: SupportSQLiteDatabase) {
		database.execSQL("CREATE UNIQUE INDEX `index_cards_table_id_phrase_id_translate` ON `cards_table` (`id_phrase`, `id_translate`);")
		database.execSQL("CREATE UNIQUE INDEX `index_module_card_id_module_id_card` ON `module_card` (`id_module`, `id_card`);")
		//database.execSQL("CREATE UNIQUE INDEX `index_error_card_id_phrase_id_translate` ON `error_card` (`id_phrase`, `id_translate`);")
	}

	fun convertByteToUUID(bytes: ByteArray): UUID {
		val buffer = ByteBuffer.wrap(bytes)
		val firstLong = buffer.long
		val secondLong = buffer.long
		return UUID(firstLong, secondLong)
	}

}