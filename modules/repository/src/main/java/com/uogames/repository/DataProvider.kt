package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.database.repository.ModuleCardRepository
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.providers.*
import kotlinx.coroutines.Dispatchers
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

	val phrase by lazy { PhraseProvider(database) }

	val images by lazy { ImageProvider(database, fileRepository) }

	val pronounce by lazy { PronunciationProvider(database, fileRepository) }

	val setting by lazy { SettingProvider(database.settingRepository) }

	val module by lazy { ModuleProvider(database.moduleRepository) }

	val moduleCard by lazy { ModuleCardProvider(database.moduleCardRepository) }

	fun clean() = ioScope.launch(Dispatchers.IO) {
		images.clear()
		pronounce.clear()
	}

}