package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
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

		suspend fun ModuleCard.toModule() = INSTANCE?.module?.getById(idModule)

		fun ModuleCard.toModuleFlow() = INSTANCE?.module?.getByIdFlow(idModule)

		suspend fun ModuleCard.toCard() = INSTANCE?.cards?.getById(idCard)

		fun ModuleCard.toCardFlow() = INSTANCE?.cards?.getByIdFlow(idCard)

		suspend fun Card.toPhrase() = INSTANCE?.phrase?.getById(idPhrase)

		fun Card.toPhraseFlow() = INSTANCE?.phrase?.getByIdFlow(idPhrase)

		suspend fun Card.toTranslate() = INSTANCE?.phrase?.getById(idTranslate)

		fun Card.toTranslateFlow() = INSTANCE?.phrase?.getByIdFlow(idTranslate)

		suspend fun Card.toImage() = idImage?.let { INSTANCE?.images?.getById(id) }

		fun Card.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun Phrase.toImage() = idImage?.let { INSTANCE?.images?.getById(it) }

		fun Phrase.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun Phrase.toPronounce() = idPronounce?.let { INSTANCE?.pronounce?.getById(it) }

		fun Phrase.toPronounceFlow() = idPronounce?.let { INSTANCE?.pronounce?.getByIdFlow(it) }
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