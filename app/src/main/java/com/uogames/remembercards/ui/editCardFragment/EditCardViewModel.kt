package com.uogames.remembercards.ui.editCardFragment

import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPronunciation
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toPronounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditCardViewModel @Inject constructor(
    private val provider: DataProvider,
    private val player: ObservableMediaPlayer
) : ViewModel() {

    private val _cardID = MutableStateFlow(0)

    private val _firstPhrase: MutableStateFlow<LocalPhrase?> = MutableStateFlow(null)
    val firstPhrase = _firstPhrase.asStateFlow()

    private val _secondPhrase: MutableStateFlow<LocalPhrase?> = MutableStateFlow(null)
    val secondPhrase = _secondPhrase.asStateFlow()

    private val _reason = MutableStateFlow("")
    val reason = _reason.asStateFlow()

    private val _clues = MutableStateFlow<List<String>>(listOf())
    val clues = _clues.asStateFlow()

    private var loadedCard: LocalCard? = null

    val preview = MutableStateFlow(false)
    val singleCard = MutableStateFlow(false)


    init {
        _reason.observe(viewModelScope) {
            if (it.length > 1) _clues.value = provider.cards.getClues(it)
        }
    }

    fun reset() {
        _firstPhrase.value = null
        _secondPhrase.value = null
        _reason.value = ""
        _cardID.value = 0
        loadedCard = null
    }

    fun resetID() {
        _cardID.value = 0
    }

    fun load(id: Int) = viewModelScope.launch {
        reset()
        val card = provider.cards.getByIdFlow(id).first()
        card?.let {
            loadedCard = it
            _cardID.value = card.id
            _firstPhrase.value = provider.phrase.getByIdFlow(card.idPhrase).first()
            _secondPhrase.value = provider.phrase.getByIdFlow(card.idTranslate).first()
            _reason.value = card.reason
        }
    }

    fun selectFirstPhrase(id: Int?) = viewModelScope.launch {
        if (id == null) {
            _firstPhrase.value = null
        } else {
            provider.phrase.getByIdFlow(id).first().let { _firstPhrase.value = it }
        }
    }

    fun selectSecondPhrase(id: Int?) = viewModelScope.launch {
        if (id == null) {
            singleCard.value = false
            _secondPhrase.value = null
        } else {
            provider.phrase.getByIdFlow(id).first().let { _secondPhrase.value = it }
        }
    }

    fun setReason(reason: String) {
        _reason.value = reason
    }

    fun play(anim: AnimationDrawable, phrase: LocalPhrase) = viewModelScope.launch { phrase.toPronounce()?.let { play(anim, it) } }

    private fun play(anim: AnimationDrawable, pronounce: LocalPronunciation) = player.play(MediaBytesSource(provider.pronounce.load(pronounce)), anim)

    fun stopPlaying() = player.stop()

    fun save(call: (Long?) -> Unit) = viewModelScope.launch {
        val card = build().ifNull { return@launch call(null) }
        val res = provider.cards.add(card)
        call(res)
    }

    fun update(call: (Boolean) -> Unit) = viewModelScope.launch {
        val card = build().ifNull { return@launch call(false) }
        val res = provider.cards.update(card)
        call(res)
    }

    fun delete(call: (Boolean) -> Unit) = viewModelScope.launch {
        val res = provider.cards.delete(LocalCard(_cardID.value))
        call(res)
    }

    private fun build(): LocalCard? {
        val id = _cardID.value
        val firstID = _firstPhrase.value?.id.ifNull { return null }
        val secondID = if (singleCard.value) firstID
        else _secondPhrase.value?.id.ifNull { return null }
        val reason = _reason.value
        return LocalCard(
            id = id,
            idPhrase = firstID,
            idTranslate = secondID,
            idImage = null,
            reason = reason,
            globalOwner = loadedCard?.globalOwner,
            globalId = loadedCard?.globalId
        )
    }

}
