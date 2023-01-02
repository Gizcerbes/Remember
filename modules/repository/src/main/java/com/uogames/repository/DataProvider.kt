package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.local.Card
import com.uogames.dto.local.ModuleCard
import com.uogames.dto.local.LocalPhrase
import com.uogames.network.NetworkProvider
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.providers.*

class DataProvider private constructor(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository,
	private val networkProvider: NetworkProvider
) {

	companion object {
		private var INSTANCE: DataProvider? = null

		fun get(context: Context, secret: () -> String, data: () -> Map<String, String>): DataProvider {
			if (INSTANCE == null) synchronized(this) {
				if (INSTANCE == null) INSTANCE = DataProvider(
					DatabaseRepository.getINSTANCE(context),
					FileRepository.getINSTANCE(context),
					NetworkProvider.getInstance(context, secret, data)
				)
			}
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

		suspend fun Card.toImage() = INSTANCE?.images?.getById(id)

		fun Card.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun LocalPhrase.toImage() = idImage?.let { INSTANCE?.images?.getById(it) }

		fun LocalPhrase.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun LocalPhrase.toPronounce() = idPronounce?.let { INSTANCE?.pronounce?.getById(it) }

		fun LocalPhrase.toPronounceFlow() = idPronounce?.let { INSTANCE?.pronounce?.getByIdFlow(it) }
	}

	val cards by lazy { CardsProvider(this, database.cardRepository, networkProvider) }

	val phrase by lazy { PhraseProvider(this, database.phraseRepository, networkProvider) }

	val images by lazy { ImageProvider(this, database.imageRepository, fileRepository, networkProvider) }

	val pronounce by lazy { PronunciationProvider(this, database.pronunciationRepository, fileRepository, networkProvider) }

	val setting by lazy { SettingProvider(this, database.settingRepository) }

	val module by lazy { ModuleProvider(this, database.moduleRepository, networkProvider) }

	val moduleCard by lazy { ModuleCardProvider(this, database.moduleCardRepository, networkProvider) }

	suspend fun clean() {
		images.clear()
		pronounce.clear()
	}

}