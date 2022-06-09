package com.uogames.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uogames.database.dao.*
import com.uogames.database.entity.*

@Database(
	entities = [
		CardEntity::class,
		PronunciationEntity::class,
		PhraseEntity::class,
		ImageEntity::class,
		NewCardEntity::class
	],
	version = 1
)
abstract class MyDatabase : RoomDatabase() {

	abstract fun cardDAO(): CardDAO

	abstract fun phraseDAO(): PhraseDAO

	abstract fun imageDAO(): ImageDAO

	abstract fun newCardDAO(): NewCardDAO

	abstract fun pronunciationDAO(): PronunciationDAO


	companion object {
		private var INSTANCE: MyDatabase? = null

		fun get(context: Context): MyDatabase {
			if (INSTANCE == null) {
				INSTANCE = Room.databaseBuilder(context, MyDatabase::class.java, "cardBase").build()
			}
			return INSTANCE as MyDatabase
		}
	}

}