package com.uogames.remembercards.ui.phrase.phrasesFragment

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.flags.Countries
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.toNull
import com.uogames.remembercards.viewmodel.PViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PhraseViewModel @Inject constructor(
    private val model: PViewModel
) : ViewModel() {

    enum class SearchingState { SEARCHING, SEARCHED, FAIL }

    private val provider = model.globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val shareNotice get() = model.globalViewModel.shareNotice

    val like = MutableStateFlow<String?>(null)
    val country = MutableStateFlow<Countries?>(null)
    val language = MutableStateFlow<Locale?>(null)
    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val cloud = MutableStateFlow(false)
    val search = MutableStateFlow(false)
    val newest = MutableStateFlow(false)

    private val _isSearching = MutableStateFlow(SearchingState.SEARCHED)
    val isSearching = _isSearching.asStateFlow()

    var recyclerStat: Parcelable? = null
    private var searchJob: Job? = null

    val adapter = PhraseAdapter(
        vm = this,
        reportCall = { gp -> reportCallList.forEach { it(gp) } },
        editCall = { id -> editCalList.forEach { it(id) } }
    )
    private val reportCallList = ArrayList<(GlobalPhrase) -> Unit>()
    private val editCalList = ArrayList<(Int) -> Unit>()

    init {
        like.observe(viewModelScope) { updateSize() }
        country.observe(viewModelScope) { updateSize() }
        language.observe(viewModelScope) { updateSize() }
        newest.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
    }

    private fun updateSize() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            runCatching {
                _isSearching.value = SearchingState.SEARCHING
                delay(100)
                val text = like.value
                val language = language.value?.isO3Language
                val country = country.value?.toString()
                _size.value = if (cloud.value) {
                    provider.phrase.countGlobal(text, language, country).toInt()
                } else {
                    provider.phrase.count(text, language, country)
                }
                _isSearching.value = SearchingState.SEARCHED
            }.onFailure {
                when (it) {
                    is CancellationException -> {}
                    else -> _isSearching.value = SearchingState.FAIL
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
        newest.value = false
        reportCallList.clear()
        editCalList.clear()
    }

    fun update() {
        updateSize()
    }

    fun addReportCall(call: (GlobalPhrase) -> Unit) = reportCallList.add(call)

    fun removeReportCall(call: (GlobalPhrase) -> Unit) = reportCallList.remove(call)

    fun addEditCall(call: (Int) -> Unit) = editCalList.add(call)

    fun removeEditCall(call: (Int) -> Unit) = editCalList.remove(call)

    suspend fun getLocalModel(position: Int) = model.getLocalModel(
        like = like.value,
        lang = language.value?.isO3Language,
        country = country.value?.toString(),
        newest = newest.value,
        position = position
    )

    suspend fun getGlobalModel(position: Int) = model.getGlobalModel(
        like = like.value,
        lang = language.value?.isO3Language,
        country = country.value?.toString(),
        position = position
    )

    fun share(phrase: LocalPhraseView, loading: (String) -> Unit) = model.share(phrase, loading)

    fun setShareAction(phrase: LocalPhraseView, loading: (String) -> Unit) = model.setShareAction(phrase, loading)

    fun stopSharing(phrase: LocalPhraseView) = model.stopSharing(phrase)

    fun getShareAction(phrase: LocalPhraseView) = model.getShareAction(phrase).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setDownloadAction(id: UUID, loading: (String, LocalPhrase?) -> Unit) = model.setDownloadAction(id, loading)

    fun stopDownloading(id: UUID) = model.stopDownloading(id)

    fun save(phraseView: GlobalPhraseView, loading: (String, LocalPhrase?) -> Unit) = model.save(phraseView, loading)

    fun showShareNotice(b: Boolean) = model.showShareNotice(b)
    fun getPicasso(context: Context) = model.getPicasso(context)


}