package com.uogames.remembercards.ui.cardFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Card
import com.uogames.dto.global.Image
import com.uogames.dto.global.Phrase
import com.uogames.dto.global.Pronunciation
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

class NetworkCardViewModel @Inject constructor(private val provider: DataProvider) : ViewModel() {

    inner class CardModel(val card: Card) {
        val phrase by lazy { viewModelScope.async(Dispatchers.IO) { getPhraseById(card.idPhrase) } }
        val translate by lazy { viewModelScope.async(Dispatchers.IO) { getPhraseById(card.idTranslate) } }
        val image by lazy { viewModelScope.async(Dispatchers.IO) { card.idImage?.let { getImageById(it) } } }
        val phrasePronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.idPronounce?.let { getPronunciationById(it) } } }
        val phrasePronounceData by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.idPronounce?.let { getPronounceData(it) } } }
        val phraseImage by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.idImage?.let { getImageById(it) } } }
        val translatePronounce by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.idPronounce?.let { getPronunciationById(it) } } }
        val translatePronounceData by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.idPronounce?.let { getPronounceData(it) } } }
        val translateImage by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.idImage?.let { getImageById(it) } } }
    }

    private val _size = MutableStateFlow(0L)
    val size = _size.asStateFlow()

    val like = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        like.observeWhile(viewModelScope, Dispatchers.IO) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                runCatching {
                    _size.value = provider.cards.countGlobal(like.value)
                }.onFailure {
                    _size.value = 0
                }
            }
        }
    }

    suspend fun getByPosition(position: Long): CardModel? {
        runCatching { return CardModel(provider.cards.getGlobal(like.value, position)) }
        return null
    }

    private suspend fun getPhraseById(id: UUID): Phrase? {
        runCatching { return provider.phrase.getGlobalById(id) }
        return null
    }

    private suspend fun getImageById(id: UUID): Image? {
        runCatching { return provider.images.getByGlobalId(id) }
        return null
    }

    private suspend fun getPronunciationById(id: UUID): Pronunciation? {
        runCatching { return provider.pronounce.getByGlobalId(id) }
        return null
    }

    private suspend fun getPronounceData(id: UUID): ByteArray? {
        runCatching { return provider.pronounce.downloadData(id) }
        return null
    }

    fun getDisplayLang(phrase: Phrase): String {
        return phrase.lang.runCatching {
            val data = split("-")
            Locale(data[0]).displayLanguage
        }.getOrDefault("")
    }

    fun getPicasso(context: Context) = provider.images.getPicasso(context)
}
