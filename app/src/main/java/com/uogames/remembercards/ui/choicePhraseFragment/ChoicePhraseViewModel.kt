package com.uogames.remembercards.ui.choicePhraseFragment

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.flags.Countries
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.toNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ChoicePhraseViewModel @Inject constructor(
    private val provider: DataProvider,
    player: ObservableMediaPlayer
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalPhraseModel(val phrase: LocalPhraseView)

    inner class GlobalPhraseModel(val phraseView: GlobalPhraseView) {
        val image = phraseView.image
        val pronounceData by lazy { viewModelScope.async { phraseView.pronounce?.let { getPronounceData(it.globalId) } } }
        val lang: String = Locale.forLanguageTag(phraseView.lang).displayLanguage
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

    val adapter = ChoicePhraseAdapter(
        vm = this,
        player = player,
        reportCall = { gp -> reportCallList.forEach { it(gp) } },
        choiceCall = { id -> choiceCallList.forEach { it(id) } }
    )
    private val reportCallList = ArrayList<(GlobalPhrase) -> Unit>()
    private val choiceCallList = ArrayList<(LocalPhrase) -> Unit>()

    init {
        like.observe(viewModelScope) { updateSize() }
        country.observe(viewModelScope) { updateSize() }
        language.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
    }

    private fun updateSize() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val text = like.value
            val language = language.value?.isO3Language
            val country = country.value?.toString()
            runCatching {
                _size.value = if (cloud.value) {
                    provider.phrase.countGlobal(text, language, country).toInt()
                } else {
                    provider.phrase.count(text, language, country)
                }
            }
        }
    }

    fun reset() {
        like.toNull()
        country.toNull()
        language.toNull()
        cloud.value = false
        search.value = false
        reportCallList.clear()
        choiceCallList.clear()
    }

    fun update() {
        updateSize()
    }

    fun addReportCall(call: (GlobalPhrase) -> Unit) = reportCallList.add(call)

    fun removeReportCall(call: (GlobalPhrase) -> Unit) = reportCallList.remove(call)

    fun addChoiceCall(call: (LocalPhrase) -> Unit) = choiceCallList.add(call)

    fun removeChoiceCall(call: (LocalPhrase) -> Unit) = choiceCallList.remove(call)

    suspend fun getLocalBookModel(position: Int): LocalPhraseModel? {
        val text = like.value
        val language = language.value?.isO3Language
        val country = country.value?.toString()
        return provider.phrase.getView(text, language, country, position)?.let { LocalPhraseModel(it) }
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

    fun setShareAction(phrase: LocalPhraseView, loading: (String) -> Unit): Boolean {
        shareActions[phrase.id]?.callback = loading
        return shareActions[phrase.id]?.job?.isActive.ifNull { false }
    }

    suspend fun getByGlobalId(uuid: UUID) =  provider.phrase.getByGlobalId(uuid)

    suspend fun getByPosition(position: Long): GlobalPhraseModel? {
        runCatching { return GlobalPhraseModel(provider.phrase.getGlobalView(
            text = like.value,
            lang = language.value?.isO3Language,
            country = country.value?.toString(),
            number = position
        )) }
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
        val job = viewModelScope.launch {
            runCatching {
                provider.phrase.save(phraseModel.phraseView)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phraseView.globalId]?.callback?.let { back -> back("Ok") }
                    downloadActions.remove(phraseModel.phraseView.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phraseView.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadActions.remove(phraseModel.phraseView.globalId)
                }
            }
        }
        downloadActions[phraseModel.phraseView.globalId] = DownloadAction(job, loading)
    }

    fun getPicasso(context: Context) = provider.images.getPicasso(context)

}