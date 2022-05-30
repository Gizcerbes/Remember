package com.uogames.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uogames.database.dao.CardDAO
import com.uogames.database.entity.CardEntity
import com.uogames.database.entity.ImageEntity
import com.uogames.database.entity.NewCardEntity
import com.uogames.database.entity.WordEntity

@Database(
	entities = [
		CardEntity::class,
		WordEntity::class,
		ImageEntity::class,
		NewCardEntity::class
	],
	version = 1
)
abstract class MyDatabase : RoomDatabase() {

	abstract fun cardDAO(): CardDAO


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