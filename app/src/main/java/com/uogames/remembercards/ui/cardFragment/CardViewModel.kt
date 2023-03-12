package com.uogames.remembercards.ui.cardFragment

import android.content.Context
import android.os.Parcelable
import android.util.Log
import com.uogames.dto.global.GlobalCard
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.global.GlobalPronunciation
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalPhrase
import com.uogames.flags.Countries
import com.uogames.map.CardMap.update
import com.uogames.map.PhraseMap.update
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.remembercards.utils.toNull
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPronounce
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CardViewModel @Inject constructor(
    //private val provider: DataProvider,
    private val globalViewModel: GlobalViewModel,
    player: ObservableMediaPlayer
) {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalCardModel(val card: LocalCard) {
        val phrase by lazy { viewModelScope.async { provider.phrase.getById(card.idPhrase) } }
        val translate by lazy { viewModelScope.async { provider.phrase.getById(card.idTranslate) } }
        val image by lazy { viewModelScope.async { card.toImage() } }
        val phrasePronounce by lazy { viewModelScope.async { phrase.await()?.toPronounce() } }
        val phraseImage by lazy { viewModelScope.async { phrase.await()?.toImage() } }
        val translatePronounce by lazy { viewModelScope.async { translate.await()?.toPronounce() } }
        val translateImage by lazy { viewModelScope.async { translate.await()?.toImage() } }
    }

    inner class GlobalCardModel(val card: GlobalCard) {
        val phrase by lazy { viewModelScope.async { getPhraseById(card.idPhrase) } }
        val translate by lazy { viewModelScope.async { getPhraseById(card.idTranslate) } }
        val image by lazy { viewModelScope.async { card.idImage?.let { getImageById(it) } } }
        val phrasePronounce by lazy { viewModelScope.async { phrase.await()?.idPronounce?.let { getPronunciationById(it) } } }
        val phrasePronounceData by lazy { viewModelScope.async { phrase.await()?.idPronounce?.let { getPronounceData(it) } } }
        val phraseImage by lazy { viewModelScope.async { phrase.await()?.idImage?.let { getImageById(it) } } }
        val translatePronounce by lazy { viewModelScope.async { translate.await()?.idPronounce?.let { getPronunciationById(it) } } }
        val translatePronounceData by lazy { viewModelScope.async { translate.await()?.idPronounce?.let { getPronounceData(it) } } }
        val translateImage by lazy { viewModelScope.async { translate.await()?.idImage?.let { getImageById(it) } } }
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadAction = HashMap<UUID, DownloadAction>()

    val shareNotice get() = globalViewModel.shareNotice

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
    val adapter = CardAdapter(
        model = this,
        player = player,
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

    fun reset() {
        like.toNull()
        languageFirst.toNull()
        languageSecond.toNull()
        countryFirst.toNull()
        countrySecond.toNull()
        cloud.value = false
        search.value = false
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


    suspend fun get(position: Int) = provider.cards.get(
        like = like.value,
        langFirst = languageFirst.value?.isO3Language,
        langSecond = languageSecond.value?.isO3Language,
        countryFirst = countryFirst.value?.toString(),
        countrySecond = countrySecond.value?.toString(),
        position = position
    )?.let { LocalCardModel(it) }

    fun share(card: LocalCard, result: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.cards.share(card.id)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    shareActions[card.id]?.callback?.let { back -> back("Ok") }
                    shareActions.remove(card.id)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    shareActions[card.id]?.callback?.let { back -> back(it.message ?: "Error") }
                    shareActions.remove(card.id)
                }
            }
        }
        shareActions[card.id] = ShareAction(job, result)
    }

    fun setShareAction(card: LocalCard, loading: (String) -> Unit): Boolean {
        shareActions[card.id]?.callback = loading
        return shareActions[card.id]?.job?.isActive.ifNull { false }
    }

    fun stopSharing(card: LocalCard) {
        val action = shareActions[card.id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        shareActions.remove(card.id)
    }

    suspend fun getByPosition(position: Long): GlobalCardModel? {
        runCatching {
            return GlobalCardModel(
                provider.cards.getGlobal(
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

    private suspend fun getPhraseById(id: UUID): GlobalPhrase? {
        runCatching { return provider.phrase.getGlobalById(id) }
        return null
    }

    private suspend fun getImageById(id: UUID): GlobalImage? {
        runCatching { return provider.images.getGlobalById(id) }
        return null
    }

    private suspend fun getPronunciationById(id: UUID): GlobalPronunciation? {
        runCatching { return provider.pronounce.getGlobalById(id) }
        return null
    }

    private suspend fun getPronounceData(id: UUID): ByteArray? {
        runCatching { return provider.pronounce.downloadData(id) }
        return null
    }

    fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
        downloadAction[id]?.callback = loading
        return downloadAction[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadAction[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        downloadAction.remove(id)
    }

    fun save(cardModel: GlobalCardModel, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                val phraseImage = cardModel.phraseImage.await()?.globalId?.let {
                    provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
                }
                val phrasePronounce = cardModel.phrasePronounce.await()?.globalId?.let {
                    provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
                }
                val phrase = cardModel.phrase.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }
                phrase?.let {
                    provider.phrase.update(it.update(cardModel.phrase.await(), phrasePronounce?.id, phraseImage?.id))
                }.ifNull { provider.phrase.add(LocalPhrase().update(cardModel.phrase.await(), phrasePronounce?.id, phraseImage?.id)) }
                val translateImage = cardModel.translateImage.await()?.globalId?.let {
                    provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
                }
                val translatePronounce = cardModel.translatePronounce.await()?.globalId?.let {
                    provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
                }
                val translate = cardModel.translate.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }
                translate?.let {
                    provider.phrase.update(it.update(cardModel.translate.await(), translatePronounce?.id, translateImage?.id))
                }.ifNull { provider.phrase.add(LocalPhrase().update(cardModel.translate.await(), translatePronounce?.id, translateImage?.id)) }

                val phraseID = cardModel.phrase.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }?.id.ifNull {
                    throw Exception("Error")
                }
                val translateID = cardModel.translate.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }?.id.ifNull {
                    throw Exception("Error")
                }
                val cardImage = cardModel.image.await()?.globalId?.let {
                    provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
                }

                provider.cards.getByGlobalId(cardModel.card.globalId)?.let {
                    provider.cards.update(it.update(cardModel.card, phraseID, translateID, cardImage?.id))
                }.ifNull {
                    provider.cards.add(LocalCard().update(cardModel.card, phraseID, translateID, cardImage?.id))
                }
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadAction[cardModel.card.globalId]?.callback?.let { back -> back("Ok") }
                    downloadAction.remove(cardModel.card.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadAction[cardModel.card.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadAction.remove(cardModel.card.globalId)
                }
            }
        }
        downloadAction[cardModel.card.globalId] = DownloadAction(job, loading)
    }


    fun showShareNotice(b: Boolean) = globalViewModel.showShareNotice(b)

    fun getPicasso(context: Context) = globalViewModel.getPicasso(context)


}