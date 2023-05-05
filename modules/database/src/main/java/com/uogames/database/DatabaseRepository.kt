package com.uogames.database

import android.content.Context
import com.uogames.database.map.*
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

    private val imageMapper = ImageViewMap()
    private val pronounceMapper = PronunciationViewMap()
    private val phraseMapper = PhraseViewMap(
        pronounceBuilder = { id: Int -> pronunciationRepository.getViewById(id) },
        imageBuilder = { id: Int -> imageRepository.getViewByID(id) }
    )
    private val cardMapper = CardViewMap(
        phraseBuilder = { id: Int -> phraseRepository.getViewById(id) ?: throw Exception("Phrase wasn't loaded") },
        imageBuilder = { id: Int -> imageRepository.getViewByID(id) }
    )
    private val moduleMapper = ModuleViewMap()
    private val moduleCardMapper = ModuleCardViewMap(
        moduleBuilder = { id -> moduleRepository.getViewById(id) ?: throw Exception("Module wasn't loaded") },
        cardBuilder = { id -> cardRepository.getViewById(id) ?: throw Exception("Card wasn't loaded") }
    )

    val phraseRepository by lazy { PhraseRepository(database.phraseDAO(), phraseMapper) }

    val imageRepository by lazy { ImageRepository(database.imageDAO(), imageMapper) }

    val pronunciationRepository by lazy { PronunciationRepository(database.pronunciationDAO(), pronounceMapper) }

    val cardRepository by lazy { CardRepository(database.cardDAO(), cardMapper) }

    val settingRepository by lazy { SettingRepository(database.settingDAO()) }

    val moduleRepository by lazy { ModuleRepository(database.moduleDAO(), moduleMapper) }

    val moduleCardRepository by lazy { ModuleCardRepository(database.moduleCardDAO(), moduleCardMapper) }

    val userRepository by lazy { UserRepository(database.userDAO()) }

    val shareRepository by lazy { ShareRepository(database.shareDAO()) }


}