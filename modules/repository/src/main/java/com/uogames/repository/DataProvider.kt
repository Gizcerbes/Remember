package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.local.Card
import com.uogames.dto.local.ModuleCard
import com.uogames.dto.local.Phrase
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.providers.*

class DataProvider private constructor(
	private val database: DatabaseRepository,
	private val fileRepository: FileRepository
) {

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

		//fun Deferred<ModuleCard?>.toModuleDeferred() = INSTANCE?.module?.getByIdAsync { await()?.idModule }

		fun ModuleCard.toModuleFlow() = INSTANCE?.module?.getByIdFlow(idModule)

		suspend fun ModuleCard.toCard() = INSTANCE?.cards?.getById(idCard)

		fun ModuleCard.toCardFlow() = INSTANCE?.cards?.getByIdFlow(idCard)

		suspend fun Card.toPhrase() = INSTANCE?.phrase?.getById(idPhrase)

		//fun Deferred<Card?>.toPhraseDeferred() = INSTANCE?.phrase?.getByIdAsync { await()?.idPhrase }

		fun Card.toPhraseFlow() = INSTANCE?.phrase?.getByIdFlow(idPhrase)

		suspend fun Card.toTranslate() = INSTANCE?.phrase?.getById(idTranslate)

		//fun Deferred<Card?>.toTranslateDeferred() = INSTANCE?.phrase?.getByIdAsync { await()?.idTranslate }

		fun Card.toTranslateFlow() = INSTANCE?.phrase?.getByIdFlow(idTranslate)

		//suspend fun Card.toImage() = idImage?.let { INSTANCE?.images?.getByIdAsync(id)?.await() }

		suspend fun Card.toImage() = INSTANCE?.images?.getById(id)

		//fun Deferred<Card?>.cardToImageDeferred() = INSTANCE?.images?.getByIdAsync { await()?.idImage }

		fun Card.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		//suspend fun Phrase.toImage() = idImage?.let { INSTANCE?.images?.getByIdAsync(it)?.await() }

		suspend fun Phrase.toImage() = idImage?.let { INSTANCE?.images?.getById(it) }

		//fun Deferred<Phrase?>.phraseToImageDeferred() = INSTANCE?.images?.getByIdAsync { await()?.idImage }

		fun Phrase.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun Phrase.toPronounce() = idPronounce?.let { INSTANCE?.pronounce?.getById(it) }

		//fun Deferred<Phrase?>.toPronounceDeferred() = INSTANCE?.pronounce?.getByIdAsync { await()?.idPronounce }

		fun Phrase.toPronounceFlow() = idPronounce?.let { INSTANCE?.pronounce?.getByIdFlow(it) }
	}

	val cards by lazy { CardsProvider(database) }

	val phrase by lazy { PhraseProvider(database.phraseRepository) }

	val images by lazy { ImageProvider(database, fileRepository) }

	val pronounce by lazy { PronunciationProvider(database, fileRepository) }

	val setting by lazy { SettingProvider(database.settingRepository) }

	val module by lazy { ModuleProvider(database.moduleRepository) }

	val moduleCard by lazy { ModuleCardProvider(database.moduleCardRepository) }

	suspend fun clean() {
		images.clear()
		pronounce.clear()
	}

}