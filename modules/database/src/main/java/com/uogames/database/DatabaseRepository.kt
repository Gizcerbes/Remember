package com.uogames.database

import android.content.Context
import com.uogames.database.map.CardMap.toDTO
import com.uogames.database.map.CardMap.toEntity
import com.uogames.database.repository.CardRepository
import com.uogames.database.repository.ImageRepository
import com.uogames.database.repository.PhraseRepository
import com.uogames.database.repository.PronunciationRepository
import com.uogames.dto.Card
import kotlinx.coroutines.flow.map

class DatabaseRepository private constructor(private val database: MyDatabase) {

	companion object {
		private var INSTANCE: DatabaseRepository? = null

		fun getINSTANCE(context: Context): DatabaseRepository {
			if (INSTANCE == null) {
				INSTANCE = DatabaseRepository(MyDatabase.get(context))
			}
			return INSTANCE as DatabaseRepository
		}
	}

	val phraseRepository by lazy { PhraseRepository(database.phraseDAO()) }

	val imageRepository by lazy { ImageRepository(database.imageDAO()) }

	val pronunciationRepository by lazy { PronunciationRepository(database.pronunciationDAO()) }

	val cardRepository by lazy { CardRepository(database.cardDAO()) }



}