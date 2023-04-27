package com.uogames.remembercards.ui.card.cardFragment

import android.content.Context
import android.os.Parcelable
import com.uogames.dto.global.*
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.flags.Countries
import com.uogames.remembercards.ui.phrase.choicePhraseFragment.ChoicePhraseViewModel
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.toNull
import com.uogames.remembercards.viewmodel.CViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CardViewModel @Inject constructor(
    private val model: CViewModel
) {
    enum class SearchingState { SEARCHING, SEARCHED, FAIL }

    private val provider = model.globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val shareNotice get() = model.globalViewModel.shareNotice

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val like = MutableStateFlow<String?>(null)
    val languageFirst = MutableStateFlow<Locale?>(null)
    val languageSecond = MutableStateFlow<Locale?>(null)
    val countryFirst = MutableStateFlow<Countries?>(null)
    val countrySecond = MutableStateFlow<Countries?>(null)

    val cloud = MutableStateFlow(false)
    val search = MutableStateFlow(false)
    val newest = MutableStateFlow(false)

    private val _isSearching = MutableStateFlow(SearchingState.SEARCHED)
    val isSearching = _isSearching.asStateFlow()

    var recyclerStat: Parcelable? = null
    val adapter = CardAdapter(
        model = this,
        reportCall = { gc -> reportCallList.forEach { it(gc) } },
        cardAction = { card -> editCalList.forEach { it(card) } }
    )

    private val reportCallList = ArrayList<(GlobalCard) -> Unit>()
    private val editCalList = ArrayList<(LocalCard) -> Unit>()

    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope) { updateSize() }
        languageFirst.observe(viewModelScope) { updateSize() }
        languageSecond.observe(viewModelScope) { updateSize() }
        countryFirst.observe(viewModelScope) { updateSize() }
        countrySecond.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
        newest.observe(viewModelScope) {
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
                    provider.cards.countGlobal(text, langFirst, langSecond, countryFirst, countrySecond).toInt()
                } else {
                    provider.cards.count(text, langFirst, langSecond, countryFirst, countrySecond)
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
        languageFirst.toNull()
        languageSecond.toNull()
        countryFirst.toNull()
        countrySecond.toNull()
        cloud.value = false
        search.value = false
        newest.value = false
        reportCallList.clear()
        editCalList.clear()
    }

    fun update() {
        updateSize()
    }

    fun addReportListener(call: (GlobalCard) -> Unit) = reportCallList.add(call)

    fun removeReportListener(call: (GlobalCard) -> Unit) = reportCallList.remove(call)

    fun addEditCall(call: (LocalCard) -> Unit) = editCalList.add(call)

    fun removeEditCall(call: (LocalCard) -> Unit) = editCalList.remove(call)

    fun getLocalModelViewAsync(position: Int) = viewModelScope.async { getLocalModelView(position) }

    suspend fun getLocalModelView(position: Int) = model.getLocalModelView(
        like = like.value,
        langFirst = languageFirst.value?.isO3Language,
        langSecond = languageSecond.value?.isO3Language,
        countryFirst = countryFirst.value?.toString(),
        countrySecond = countrySecond.value?.toString(),
        newest = newest.value,
        position = position
    )

    fun getGlobalModelViewAsync(position: Long) = viewModelScope.async { getGlobalModelView(position) }

    suspend fun getGlobalModelView(position: Long): CViewModel.GlobalCardModel? {
        runCatching {
            return model.getGlobalModelView(
                text = like.value,
                langFirst = languageFirst.value?.isO3Language,
                langSecond = languageSecond.value?.isO3Language,
                countryFirst = countryFirst.value?.toString(),
                countrySecond = countrySecond.value?.toString(),
                number = position
            )
        }
        return null
    }

    fun share(card: LocalCardView, result: (String) -> Unit) = model.share(card, result)

    fun setShareAction(card: LocalCardView, loading: (String) -> Unit) = model.setShareAction(card, loading)

    fun stopSharing(card: LocalCardView) = model.stopSharing(card)


    fun setDownloadAction(id: UUID, loading: (String, LocalCard?) -> Unit) = model.setDownloadAction(id, loading)

    fun stopDownloading(id: UUID) = model.stopDownloading(id)

    fun save(cardModel: GlobalCardView, loading: (String, LocalCard?) -> Unit) = model.save(cardModel, loading)


    fun showShareNotice(b: Boolean) = model.showShareNotice(b)

    fun getPicasso(context: Context) = model.getPicasso(context)


}