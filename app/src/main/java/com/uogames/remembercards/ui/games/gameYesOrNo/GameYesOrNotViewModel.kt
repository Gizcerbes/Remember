package com.uogames.remembercards.ui.games.gameYesOrNo

import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import com.uogames.dto.local.ErrorCard
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.viewmodel.GlobalViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.collections.ArrayList

class GameYesOrNotViewModel @Inject constructor(
    val globalViewModel: GlobalViewModel,
    private val player: ObservableMediaPlayer
) : ViewModel() {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val MAX_TIME = 60000
    }

    inner class LocalCardModel(val card: LocalCardView) {
        private val pData by lazy { viewModelScope.async { card.phrase.pronounce?.let { provider.pronounce.load(it) } } }
        private val tData by lazy { viewModelScope.async { card.translate.pronounce?.let { provider.pronounce.load(it) } } }

        suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData.await()), anim)
        suspend fun playTranslate(anim: AnimationDrawable) = player.play(MediaBytesSource(tData.await()), anim)
    }

    data class AnswerCard(val first: LocalCardModel, val second: LocalCardModel)

    val module: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _timer = MutableStateFlow(MAX_TIME)
    val time = _timer.asStateFlow()

    private val _trueAnswers = MutableStateFlow(0)
    val trueAnswers = _trueAnswers.asStateFlow()

    private val _isStarted = MutableStateFlow(false)
    val isStarted = _isStarted.asStateFlow()

    private val _answerMap = HashMap<Int, Pair<LocalCardModel, ArrayList<LocalCardModel>>>()


    private val _allAnswers = MutableStateFlow(0)
    val allAnswers = _allAnswers.asStateFlow()

    private val _answerCard = MutableStateFlow<AnswerCard?>(null)
    val answerCard = _answerCard.asStateFlow()

    private val _isTrueAnswer = MutableStateFlow<Boolean?>(null)

    private var job: Job? = null

    fun reset() {
        _timer.value = MAX_TIME
        _allAnswers.value = 0
        _trueAnswers.value = 0
        _answerMap.clear()
    }

    fun getAnswers() = _answerMap.toList().sortedByDescending { p -> p.second.second.size }.map { it.second }

    fun start(endCall: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch {
            _isStarted.value = true
            while (true) {
                delay(1000)
                _timer.value -= 1000
                if (_timer.value <= 0) {
                    launch(Dispatchers.Main) { endCall() }
                    return@launch
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        _isStarted.value = false
    }

    fun newAnswer() = viewModelScope.launch {
        val f = module.value?.let {
            if (Math.random() > 0.25) provider.moduleCard.getRandomModuleView(it)?.card
            else provider.moduleCard.getUnknowable(it)?.card
        }.ifNull {
            if (Math.random() > 0.25) provider.cards.getUnknowableView()
            else provider.cards.getRandomView()
        }?.let {
            LocalCardModel(it)
        }.ifNull { return@launch }

        val s = if (Math.random() > 0.5)
            module.value?.let {
                if (Math.random() > 0.5) {
                    provider.moduleCard.let { mc ->
                        mc.getConfusing(it, f.card.phrase.id).ifNull { mc.getRandomModuleView(it) }
                    }?.card
                } else provider.moduleCard.getRandomModuleView(it)?.card
            }.ifNull {
                if (Math.random() > 0.5) {
                    provider.cards.let { c ->
                        c.getConfusingView(f.card.phrase.id).ifNull { c.getRandomView() }
                    }
                } else provider.cards.getRandomView()
            }?.let {
                LocalCardModel(it)
            }.ifNull { return@launch }
        else f

        _answerCard.value = AnswerCard(f, s)
    }

    fun check(card: AnswerCard, b: Boolean): Boolean {

        val r = (card.first.card.id == card.second.card.id) == b
        val list = _answerMap[card.first.card.id]?.second.ifNull {
            val l = ArrayList<LocalCardModel>()
            _answerMap[card.first.card.id] = card.first to l
            return@ifNull l
        }
        if (!r) list.add(card.second)
        if (r) _trueAnswers.value++
        _allAnswers.value++
        _isTrueAnswer.value = r
        addToErrorCard(card, r)
        return r
    }

    private fun addToErrorCard(card: AnswerCard, result: Boolean) = viewModelScope.launch {
        val trues = provider.errorCardProvider.getByPhraseAndTranslate(card.first.card.phrase.id, card.first.card.translate.id)
        val err = provider.errorCardProvider.getByPhraseAndTranslate(card.first.card.phrase.id, card.second.card.translate.id)
        if (err != null) {
            provider.errorCardProvider.update(updateErrorCard(err, result))
        } else {
            val nc = updateErrorCard(ErrorCard(idPhrase = card.first.card.phrase.id, idTranslate = card.second.card.translate.id), result)
            if (nc.percentCorrect < 100) provider.errorCardProvider.add(nc)
        }
        if (trues != null) {
            provider.errorCardProvider.update(updateErrorCard(trues, result))
        } else {
            val nc = updateErrorCard(ErrorCard(idPhrase = card.first.card.phrase.id, idTranslate = card.first.card.translate.id), result)
            if (nc.percentCorrect < 100) provider.errorCardProvider.add(nc)
        }
    }

    private fun updateErrorCard(errorCard: ErrorCard, result: Boolean): ErrorCard {
        if (result) errorCard.correct++ else errorCard.incorrect++
        errorCard.percentCorrect = (errorCard.correct * 100 / (errorCard.correct + errorCard.incorrect)).toByte()
        return errorCard
    }
}
