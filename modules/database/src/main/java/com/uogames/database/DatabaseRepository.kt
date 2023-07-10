package com.uogames.database

import android.content.Context
import com.uogames.database.repository.*

class DatabaseRepository private constructor(private val database: MyDatabase) {

    companion object {
        private var INSTANCE: DatabaseRepository? = null

        fun getINSTANCE(context: Context): DatabaseRepository {
            if (INSTANCE == null) synchronized(this) {
                if (INSTANCE == null) INSTANCE = DatabaseRepository(MyDatabase.get(context))
            }
            return INSTANCE as DatabaseRepository
        }
    }


    val phraseRepository by lazy { PhraseRepository(database.phraseDAO()) }

    val imageRepository by lazy { ImageRepository(database.imageDAO()) }

    val pronunciationRepository by lazy { PronunciationRepository(database.pronunciationDAO()) }

    val cardRepository by lazy { CardRepository(database.cardDAO()) }

    val settingRepository by lazy { SettingRepository(database.settingDAO()) }

    val moduleRepository by lazy { ModuleRepository(database.moduleDAO()) }

    val moduleCardRepository by lazy { ModuleCardRepository(database.moduleCardDAO()) }

    val userRepository by lazy { UserRepository(database.userDAO()) }

    val shareRepository by lazy { ShareRepository(database.shareDAO()) }

    val downloadRepository by lazy { DownloadRepository(database.downloadDAO()) }

    val errorCardRepository by lazy { ErrorCardRepository(database.errorCardDAO()) }




}