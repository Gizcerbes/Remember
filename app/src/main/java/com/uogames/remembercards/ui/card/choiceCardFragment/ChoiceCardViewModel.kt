package com.uogames.remembercards.ui.card.choiceCardFragment

import android.content.Context
import android.os.Parcelable
import com.uogames.dto.global.*
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.flags.Countries
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ChoiceCardViewModel @Inject constructor(
    private val globalViewModel: GlobalViewModel,
    player: ObservableMediaPlayer
) {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    inner class LocalCardModel(val card: LocalCardView)

    inner class GlobalCardModel(val card: GlobalCardView) {
        val phrasePronounceData by lazy { viewModelScope.async { card.phrase.pronounce?.globalId?.let { getPronounceData(it) } } }
        val translatePronounceData by lazy { viewModelScope.async { card.translate.pronounce?.globalId?.let { getPronounceData(it) } } }
    }

    private class DownloadAction(val job: Job, var callback: (String, LocalCard?) -> Unit)
    private val downloadAction = HashMap<UUID, DownloadAction>()

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val like = MutableStateFlow<String?>(null)
    val languageFirst = MutableStateFlow<Locale?>(null)
    val languageSecond = MutableStateFlow<Locale?>(null)
    val countryFirst = MutableStateFlow<Countries?>(null)
    val countrySecond = MutableStateFlow<Countries?>(null)

    val cloud = MutableStateFlow(false)
    val search = MutableStateFlow(false)

    var recyclerStat: Parcelable? = null
    val adapter = ChoiceCardAdapter(
        model = this,
        player = player,
        reportCall = { gc -> reportCallList.forEach { it(gc) } },
        cardAction = { card -> editCalList.forEach { it(card) } }
    )

    private val reportCallList = ArrayList<(GlobalCard) -> Unit>()
    private val editCalList = ArrayList<(LocalCard) -> Unit>()

    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope){ updateSize() }
        languageFirst.observe(viewModelScope) { updateSize() }
        languageSecond.observe(viewModelScope) { updateSize() }
        countryFirst.observe(viewModelScope) { updateSize() }
        countrySecond.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
    }

    private fun updateSize() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(100)
            val text = like.value
            val langFirst = languageFirst.value?.isO3Language
            val langSecond = languageSecond.value?.isO3Language
            val countryFirst = countryFirst.value?.toString()
            val countrySecond = countrySecond.value?.toString()
            runCatching {
                _size.value = if (cloud.value) {
                    val res = provider.cards.countGlobal(text, langFirst, langSecond, countryFirst, countrySecond)
                    res.toInt()
                } else {
                    provider.cards.count(text, langFirst, langSecond, countryFirst, countrySecond)
                }
            }
        }
    }

    fun reset(){
        like.value = null
        languageFirst.value = null
        languageSecond.value = null
        countryFirst.value = null
        countrySecond.value = null
        cloud.value = false
        search.value = false
    }

    fun update(){
        updateSize()
    }

    fun addReportListener(call: (GlobalCard) -> Unit) = reportCallList.add(call)

    fun removeReportListener(call: (GlobalCard) -> Unit) = reportCallList.remove(call)

    fun addChoiceListener(call: (LocalCard) -> Unit) = editCalList.add(call)

    fun removeChoiceListener(call: (LocalCard) -> Unit) = editCalList.remove(call)

    fun getViewAsync(position: Int) = viewModelScope.async { getView(position) }

    suspend fun getView(position: Int) = provider.cards.getView(
        like = like.value,
        langFirst = languageFirst.value?.isO3Language,
        langSecond = languageSecond.value?.isO3Language,
        countryFirst = countryFirst.value?.toString(),
        countrySecond = countrySecond.value?.toString(),
        position = position
    )?.let { LocalCardModel(it) }

    suspend fun getByGlobalId(uuid: UUID) = viewModelScope.async { provider.cards.getByGlobalId(uuid) }.await()

    fun getGlobalViewAsync(position: Long) = viewModelScope.async { getByPosition(position) }

    suspend fun getByPosition(position: Long): GlobalCardModel? {
        runCatching {
            return GlobalCardModel(
                provider.cards.getGlobalView(
                    text = like.value,
                    langFirst = languageFirst.value?.isO3Language,
                    langSecond = languageSecond.value?.isO3Language,
                    countryFirst = countryFirst.value?.toString(),
                    countrySecond = countrySecond.value?.toString(),
                    number = position
                )
            )
        }
        return null
    }

    private suspend fun getPronounceData(id: UUID): ByteArray? {
        runCatching { return provider.pronounce.downloadData(id) }
        return null
    }

    fun setDownloadAction(id: UUID, loading: (String, LocalCard?) -> Unit): Boolean {
        downloadAction[id]?.callback = loading
        return downloadAction[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadAction[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel", null)
        downloadAction.remove(id)
    }

    fun save(cardModel: GlobalCardModel, loading: (String, LocalCard?) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.cards.save(cardModel.card)
            }.onSuccess { lc ->
                launch(Dispatchers.Main) {
                    downloadAction[cardModel.card.globalId]?.callback?.let { back -> back("Ok", lc) }
                    downloadAction.remove(cardModel.card.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadAction[cardModel.card.globalId]?.callback?.let { back -> back(it.message ?: "Error", null) }
                    downloadAction.remove(cardModel.card.globalId)
                }
            }
        }
        downloadAction[cardModel.card.globalId] = DownloadAction(job, loading)
    }


    fun getPicasso(context: Context) = provider.images.getPicasso(context)

}