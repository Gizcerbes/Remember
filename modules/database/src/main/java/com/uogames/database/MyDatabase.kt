package com.uogames.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uogames.database.dao.CardDAO
import com.uogames.database.entity.CardEntity

@Database(entities = [CardEntity::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    abstract fun cardDAO() : CardDAO

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