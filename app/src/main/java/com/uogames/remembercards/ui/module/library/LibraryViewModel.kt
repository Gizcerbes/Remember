package com.uogames.remembercards.ui.module.library

import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalModuleView
import com.uogames.flags.Countries
import com.uogames.remembercards.ui.phrase.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.viewmodel.MViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LibraryViewModel @Inject constructor(
    private val model: MViewModel
) {

    enum class SearchingState { SEARCHING, SEARCHED, FAIL }

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val shareNotice get() = model.globalViewModel.shareNotice

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val like = MutableStateFlow<String?>(null)
    val languageFirst = MutableStateFlow<Locale?>(null)
    val languageSecond = MutableStateFlow<Locale?>(null)
    val countryFirst = MutableStateFlow<Countries?>(null)
    val countrySecond = MutableStateFlow<Countries?>(null)
    val search = MutableStateFlow(false)
    val cloud = MutableStateFlow(false)
    val newrst = MutableStateFlow(false)

    private val _isSearching = MutableStateFlow(SearchingState.SEARCHED)
    val isSearching = _isSearching.asStateFlow()

    val adapter = LibraryAdapter(this)
    private val editCall = ArrayList<(Int) -> Unit>()
    private val reportCall = ArrayList<(GlobalModuleView) -> Unit>()
    private val watchLocalCall = ArrayList<(Int) -> Unit>()
    private val watchGlobalCall = ArrayList<(UUID) -> Unit>()

    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope) { updateSize() }
        search.observe(viewModelScope) { updateSize() }
        languageFirst.observe(viewModelScope) { updateSize() }
        languageSecond.observe(viewModelScope) { updateSize() }
        countryFirst.observe(viewModelScope) { updateSize() }
        countrySecond.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
        newrst.observe(viewModelScope) {
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
                val langFirst = languageFirst.value?.isO3Language
                val langSecond = languageSecond.value?.isO3Language
                val countryFirst = countryFirst.value?.toString()
                val countrySecond = countrySecond.value?.toString()
                _size.value = if (cloud.value) {
                    model.getGlobalSize(text, langFirst, langSecond, countryFirst, countrySecond)
                } else {
                    model.getLocalSize(text, langFirst, langSecond, countryFirst, countrySecond)
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
        like.value = null
        search.value = false
        cloud.value = false
        editCall.clear()
        reportCall.clear()
        watchGlobalCall.clear()
        watchLocalCall.clear()
    }

    fun update() {
        updateSize()
    }

    fun addEditCall(call: (Int) -> Unit) = editCall.add(call)

    fun removeEditCall(call: (Int) -> Unit) = editCall.remove(call)

    fun edit(view: LocalModuleView) = editCall.forEach { it(view.id) }

    fun addReportCall(call: (GlobalModuleView) -> Unit) = reportCall.add(call)

    fun removeReportCall(call: (GlobalModuleView) -> Unit) = reportCall.remove(call)

    fun report(view: GlobalModuleView) = reportCall.forEach { it(view) }

    fun addWatchLocalCall(call: (Int) -> Unit) = watchLocalCall.add(call)

    fun removeWatchLocalCall(call: (Int) -> Unit) = watchLocalCall.remove(call)

    fun watchLocal(view: LocalModuleView) = watchLocalCall.forEach { it(view.id) }

    fun addWatchGlobalCall(call: (UUID) -> Unit) = watchGlobalCall.add(call)

    fun removeWatchGlobalCall(call: (UUID) -> Unit) = watchGlobalCall.remove(call)

    fun watchGlobal(view: GlobalModuleView) = watchGlobalCall.forEach { it(view.globalId) }

    fun createModule(name: String, call: (Int) -> Unit) = model.createModule(name, call)

    suspend fun getLocalModel(position: Int) = model.getLocalModel(
        text = like.value,
        langFirst = languageFirst.value?.isO3Language,
        langSecond = languageSecond.value?.isO3Language,
        countryFirst = countryFirst.value?.toString(),
        countrySecond = countrySecond.value?.toString(),
        newest = newrst.value,
        position = position
    )

    fun share(module: LocalModuleView, loading: (String) -> Unit) = model.share(module, loading)

    fun setShareAction(module: LocalModuleView, loading: (String) -> Unit) = model.setShareAction(module, loading)

    fun stopSharing(module: LocalModuleView, message: String = "Cancel") = model.stopSharing(module, message)

    suspend fun getGlobalModel(position: Int) = model.getGlobalModel(
        text = like.value,
        langFirst = languageFirst.value?.isO3Language,
        langSecond = languageSecond.value?.isO3Language,
        countryFirst = countryFirst.value?.toString(),
        countrySecond = countrySecond.value?.toString(),
        position = position
    )

    fun download(view: GlobalModuleView, loading: (String) -> Unit) = model.download(view, loading)

    fun setDownloadAction(id: UUID, loading: (String) -> Unit) = model.setDownloadAction(id, loading)

    fun stopDownloading(id: UUID) = model.stopDownloading(id)

    fun showShareNotice(b: Boolean) = model.globalViewModel.showShareNotice(b)

}