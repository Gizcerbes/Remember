package com.uogames.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uogames.database.dao.*
import com.uogames.database.entity.*
import com.uogames.database.migrations.MigrationFrom1To2
import com.uogames.database.migrations.MigrationFrom2To3
import com.uogames.database.migrations.MigrationFrom3To4
import com.uogames.database.migrations.MigrationFrom6To7

@Database(
	entities = [
		PronunciationEntity::class,
		PhraseEntity::class,
		ImageEntity::class,
		CardEntity::class,
		SettingEntity::class,
		ModuleEntity::class,
		ModuleCardEntity::class,
		ErrorCardEntity::class,
		UserEntity::class,
		ShareEntity::class,
		DownloadEntity::class
	],
	autoMigrations = [
		AutoMigration(from = 4, to = 5),
		AutoMigration(from = 5, to = 6),
		AutoMigration(from = 7, to = 8)
	],
	version = 8
)
abstract class MyDatabase : RoomDatabase() {

	abstract fun phraseDAO(): PhraseDAO

	abstract fun imageDAO(): ImageDAO

	abstract fun cardDAO(): CardDAO

	abstract fun pronunciationDAO(): PronunciationDAO

	abstract fun settingDAO(): SettingDAO

	abstract fun moduleDAO(): ModuleDAO

	abstract fun moduleCardDAO(): ModuleCardDAO

	abstract fun errorCardDAO(): ErrorCardDAO

	abstract fun userDAO(): UserDAO

	abstract fun shareDAO(): ShareDAO

	abstract fun downloadDAO(): DownloadDAO

	companion object {
		private var INSTANCE: MyDatabase? = null

		fun get(context: Context): MyDatabase {
			if (INSTANCE == null) synchronized(this) {
				if (INSTANCE == null) INSTANCE = Room
					.databaseBuilder(context, MyDatabase::class.java, "cardBase")
					.addMigrations(
						MigrationFrom1To2,
						MigrationFrom2To3,
						MigrationFrom3To4,
						MigrationFrom6To7
					)
					.build()
			}
			return INSTANCE as MyDatabase
		}

	}


}