package com.uogames.remembercards.ui.choicePhraseFragment

import android.content.Context
import android.os.Parcelable
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.local.LocalPhrase
import com.uogames.flags.Countries
import com.uogames.map.PhraseMap.update
import com.uogames.remembercards.ui.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ChoicePhraseViewModel @Inject constructor(
    private val provider: DataProvider
) {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalBookModel(val phrase: LocalPhrase) {
        val pronounce by lazy { viewModelScope.async { phrase.idPronounce?.let { provider.pronounce.getById(it) } } }
        val image by lazy { viewModelScope.async { phrase.idImage?.let { provider.images.getById(it) } } }
        val lang: String by lazy { Locale.forLanguageTag(phrase.lang).displayLanguage }
    }
    inner class GlobalPhraseModel(val phrase: GlobalPhrase) {
        val image by lazy { viewModelScope.async { phrase.idImage?.let { getImageById(it) } } }
        val pronounceData by lazy { viewModelScope.async { phrase.idPronounce?.let { getPronounceData(it) } } }
        val lang by lazy { Locale.forLanguageTag(phrase.lang).displayLanguage }
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String) -> Unit)
    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadActions = HashMap<UUID, DownloadAction>()

    val like = MutableStateFlow<String?>(null)
    val country = MutableStateFlow<Countries?>(null)
    val language = MutableStateFlow<Locale?>(null)
    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val cloud = MutableStateFlow(false)
    val search = MutableStateFlow(false)

    var recyclerStat: Parcelable? = null
    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope) { getSize() }
        country.observe(viewModelScope) { getSize() }
        language.observe(viewModelScope) { getSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            getSize()
        }
    }

    private suspend fun getSize() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val text = like.value.ifNull { "" }
            val language = language.value?.isO3Language
            val country = country.value?.toString()
            try {
                _size.value = if (cloud.value) {
                    provider.phrase.countGlobal(text).toInt()
                } else {
                    provider.phrase.count(text,language,country)
//                    if (language != null && country != null) provider.phrase.countFlow(text, language, country).first()
//                    else if (language != null) provider.phrase.countFlow(text, language).first()
//                    else if (text.isNotEmpty()) provider.phrase.countFlow(text).first()
//                    else provider.phrase.countFlow().first()
                }
            } catch (e: Exception) {
                _size.value = 0
            }
        }
    }

    fun reset() {
        like.value = ""
        country.value = null
        language.value = null
    }

    fun update(){
        viewModelScope.launch { getSize() }
    }

    suspend fun getLocalBookModel(position: Int): ChoicePhraseViewModel.LocalBookModel? {
        val text = like.value
        val language = language.value?.isO3Language
        val country = country.value?.toString()
        return provider.phrase.get(text,language,country,position)?.let { LocalBookModel(it) }
//        return if (language != null && country != null) provider.phrase.get(text, language, country, position)?.let { LocalBookModel(it) }
//        else if (language != null) provider.phrase.get(text, language, position)?.let { LocalBookModel(it) }
//        else provider.phrase.get(text, position)?.let { LocalBookModel(it) }
    }

    fun share(phrase: LocalPhrase, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.phrase.share(phrase.id)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    shareActions[phrase.id]?.callback?.let { back -> back("Ok") }
                    shareActions.remove(phrase.id)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    shareActions[phrase.id]?.callback?.let { back -> back(it.message ?: "Error") }
                    shareActions.remove(phrase.id)
                }
            }
        }
        shareActions[phrase.id] = ShareAction(job, loading)
    }

    fun setShareAction(phrase: LocalPhrase, loading: (String) -> Unit): Boolean {
        shareActions[phrase.id]?.callback = loading
        return shareActions[phrase.id]?.job?.isActive.ifNull { false }
    }

    fun stopSharing(phrase: LocalPhrase) {
        val action = shareActions[phrase.id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        shareActions.remove(phrase.id)
    }

    suspend fun getByGlobalId(uuid: UUID) = viewModelScope.async { provider.phrase.getByGlobalId(uuid) }.await()

    suspend fun getByPosition(position: Long): ChoicePhraseViewModel.GlobalPhraseModel? {
        runCatching { return GlobalPhraseModel(provider.phrase.getGlobal(like.value.toString(), position)) }
        return null
    }

    private suspend fun getImageById(id: UUID): GlobalImage? {
        runCatching { return provider.images.getGlobalById(id) }
        return null
    }

    private suspend fun getPronounceData(id: UUID): ByteArray? {
        runCatching { return provider.pronounce.downloadData(id) }
        return null
    }

    fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
        downloadActions[id]?.callback = loading
        return downloadActions[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadActions[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        downloadActions.remove(id)
    }

    fun save(phraseModel: GlobalPhraseModel, loading: (String) -> Unit) {
        val job = viewModelScope.launch{
            runCatching {
                val image = phraseModel.phrase.idImage?.let {
                    provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
                }
                val pronounce = phraseModel.phrase.idPronounce?.let {
                    provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
                }
                provider.phrase.getByGlobalId(phraseModel.phrase.globalId)?.let {
                    provider.phrase.update(it.update(phraseModel.phrase, pronounce?.id, image?.id))
                }.ifNull {
                    provider.phrase.add(com.uogames.dto.local.LocalPhrase().update(phraseModel.phrase, pronounce?.id, image?.id))
                }
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phrase.globalId]?.callback?.let { back -> back("Ok") }
                    downloadActions.remove(phraseModel.phrase.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phrase.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadActions.remove(phraseModel.phrase.globalId)
                }
            }
        }
        downloadActions[phraseModel.phrase.globalId] = DownloadAction(job, loading)
    }

    fun getPicasso(context: Context) = provider.images.getPicasso(context)

}