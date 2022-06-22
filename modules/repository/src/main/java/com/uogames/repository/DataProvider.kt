package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import com.uogames.repository.fileRepository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DataProvider private constructor(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) : Provider() {

	companion object {
		private var INSTANCE: DataProvider? = null


		fun get(context: Context): DataProvider {
			if (INSTANCE == null) INSTANCE = DataProvider(
				DatabaseRepository.getINSTANCE(context),
				FileRepository.getINSTANCE(context)
			)
			return INSTANCE as DataProvider
		}
	}

	val cards by lazy { CardsProvider(database) }

	val phrase by lazy { PhraseProvider(database ) }

	val images by lazy { ImageProvider(database, fileRepository) }

	val pronounce by lazy { PronunciationProvider(database,fileRepository) }


	fun clean() = ioScope.launch(Dispatchers.IO) {
		images.clear()
		pronounce.clear()
	}

}