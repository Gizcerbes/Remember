package com.uogames.repository

import android.content.Context
import com.uogames.database.DatabaseRepository
import com.uogames.dto.Card
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.providers.*
import kotlinx.coroutines.Deferred
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


		suspend fun ModuleCard.toModule() = INSTANCE?.module?.getByIdAsync(idModule)?.await()

		fun Deferred<ModuleCard?>.toModuleDeferred() = INSTANCE?.module?.getByIdAsync { await()?.idModule }

		fun ModuleCard.toModuleFlow() = INSTANCE?.module?.getByIdFlow(idModule)

		suspend fun ModuleCard.toCard() = INSTANCE?.cards?.getByIdAsync(idCard)?.await()

		fun Deferred<ModuleCard?>.toCardDeferred() = INSTANCE?.cards?.getByIdAsync { await()?.idCard }

		fun ModuleCard.toCardFlow() = INSTANCE?.cards?.getByIdFlow(idCard)

		suspend fun Card.toPhrase() = INSTANCE?.phrase?.getByIdAsync(idPhrase)?.await()

		fun Deferred<Card?>.toPhraseDeferred() = INSTANCE?.phrase?.getByIdAsync { await()?.idPhrase }

		fun Card.toPhraseFlow() = INSTANCE?.phrase?.getByIdFlow(idPhrase)

		suspend fun Card.toTranslate() = INSTANCE?.phrase?.getByIdAsync(idTranslate)?.await()

		fun Deferred<Card?>.toTranslateDeferred() = INSTANCE?.phrase?.getByIdAsync { await()?.idTranslate }

		fun Card.toTranslateFlow() = INSTANCE?.phrase?.getByIdFlow(idTranslate)

		suspend fun Card.toImage() = idImage?.let { INSTANCE?.images?.getByIdAsync(id)?.await() }

		fun Deferred<Card?>.cardToImageDeferred() = INSTANCE?.images?.getByIdAsync { await()?.idImage }

		fun Card.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun Phrase.toImage() = idImage?.let { INSTANCE?.images?.getByIdAsync(it)?.await() }

		fun Deferred<Phrase?>.phraseToImageDeferred() = INSTANCE?.images?.getByIdAsync { await()?.idImage }

		fun Phrase.toImageFlow() = idImage?.let { INSTANCE?.images?.getByIdFlow(it) }

		suspend fun Phrase.toPronounce() = idPronounce?.let { INSTANCE?.pronounce?.getByIdAsync(it)?.await() }

		fun Deferred<Phrase?>.toPronounceDeferred() = INSTANCE?.pronounce?.getByIdAsync { await()?.idPronounce }

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