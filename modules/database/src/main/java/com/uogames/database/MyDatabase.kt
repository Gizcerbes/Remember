package com.uogames.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uogames.database.dao.*
import com.uogames.database.entity.*

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
		UserEntity::class
	],
	version = 3
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


	companion object {
		private var INSTANCE: MyDatabase? = null

		fun get(context: Context): MyDatabase {
			if (INSTANCE == null) synchronized(this) {
				if (INSTANCE == null) INSTANCE = Room
					.databaseBuilder(context, MyDatabase::class.java, "cardBase")
					.addMigrations(
						UserEntity.migration_1_2(),
						PhraseEntity.migration_2_3()
					)
					.build()
			}
			return INSTANCE as MyDatabase
		}

	}


}