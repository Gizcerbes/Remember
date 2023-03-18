package com.uogames.repository

import android.content.Context
import com.uogames.Singleton
import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.DatabaseRepository
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalModuleCard
import com.uogames.dto.local.LocalPhrase
import com.uogames.repository.fileRepository.FileRepository
import com.uogames.repository.providers.*

class DataProvider private constructor(
    private val database: DatabaseRepository,
    private val fileRepository: FileRepository,
    private val networkProvider: NetworkProvider
) {

    companion object {
        private val dp = Singleton<DataProvider>()

        fun get(
            context: Context,
            secret: () -> String,
            data: (() -> Map<String, String>)?
        ) = dp.get {
            DataProvider(
                DatabaseRepository.getINSTANCE(context),
                FileRepository.getINSTANCE(context),
                NetworkProvider.getInstance(context, secret, data)
            )
        }

        suspend fun LocalModuleCard.toModule() = dp.get()?.module?.getById(idModule)

        fun LocalModuleCard.toModuleFlow() = dp.get()?.module?.getByIdFlow(idModule)

        suspend fun LocalModuleCard.toCard() = dp.get()?.cards?.getById(idCard)

        fun LocalModuleCard.toCardFlow() = dp.get()?.cards?.getByIdFlow(idCard)

        suspend fun LocalCard.toPhrase() = dp.get()?.phrase?.getById(idPhrase)

        fun LocalCard.toPhraseFlow() = dp.get()?.phrase?.getByIdFlow(idPhrase)

        suspend fun LocalCard.toTranslate() = dp.get()?.phrase?.getById(idTranslate)

        fun LocalCard.toTranslateFlow() = dp.get()?.phrase?.getByIdFlow(idTranslate)

        suspend fun LocalCard.toImage() = dp.get()?.images?.getById(id)

        fun LocalCard.toImageFlow() = idImage?.let { dp.get()?.images?.getByIdFlow(it) }

        suspend fun LocalPhrase.toImage() = idImage?.let { dp.get()?.images?.getById(it) }

        fun LocalPhrase.toImageFlow() = idImage?.let { dp.get()?.images?.getByIdFlow(it) }

        suspend fun LocalPhrase.toPronounce() =
            idPronounce?.let { dp.get()?.pronounce?.getById(it) }

        fun LocalPhrase.toPronounceFlow() =
            idPronounce?.let { dp.get()?.pronounce?.getByIdFlow(it) }
    }

    val cards by lazy { CardsProvider(this, database.cardRepository, networkProvider) }

    val phrase by lazy { PhraseProvider(this, database.phraseRepository, networkProvider) }

    val images by lazy { ImageProvider(this, database.imageRepository, fileRepository, networkProvider) }

    val pronounce by lazy { PronunciationProvider(this, database.pronunciationRepository, fileRepository, networkProvider) }

    val setting by lazy { SettingProvider(this, database.settingRepository) }

    val module by lazy { ModuleProvider(this, database.moduleRepository, networkProvider) }

    val moduleCard by lazy { ModuleCardProvider(this, database.moduleCardRepository, networkProvider) }

    val report by lazy { ReportProvider(networkProvider) }

    val user by lazy { UserProvider(this, database.userRepository, networkProvider) }

    suspend fun clean() {
        images.clear()
        pronounce.clear()
    }

}