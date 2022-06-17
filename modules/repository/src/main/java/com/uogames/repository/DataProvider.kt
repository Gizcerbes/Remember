package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DataProvider private constructor(
	private val database: DatabaseRepository
) : Provider() {

	companion object {
		private var INSTANCE: DataProvider? = null


		fun get(context: Context): DataProvider {
			if (INSTANCE == null) INSTANCE = DataProvider(
				DatabaseRepository.getINSTANCE(context)
			)
			return INSTANCE as DataProvider
		}
	}

	val cards by lazy { CardsProvider(database) }

	val phrase by lazy { PhraseProvider(database) }

	val images by lazy { ImageProvider(database) }

	val pronounce by lazy { PronunciationProvider(database) }


	fun clean() = ioScope.launch {
		images.cleanAsync().await()
		pronounce.cleanAsync().await()
	}

}