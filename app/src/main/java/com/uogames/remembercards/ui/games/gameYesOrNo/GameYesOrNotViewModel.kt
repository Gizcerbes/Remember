package com.uogames.remembercards.ui.games.gameYesOrNo

import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.repository.DataProvider.Companion.toCard
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

    data class AnswersCard(val firs: LocalCard, val second: LocalCard, var truth: Boolean)

    data class AnswerCards2(val first: LocalCardModel, val second: LocalCardModel)

    val module: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _timer = MutableStateFlow(MAX_TIME)
    val time = _timer.asStateFlow()

    private val _trueAnswers = MutableStateFlow(0)
    val trueAnswers = _trueAnswers.asStateFlow()

    private val _isStarted = MutableStateFlow(false)
    val isStarted = _isStarted.asStateFlow()

    private val _answersList = ArrayList<AnswersCard>()
    private val _answerMap = HashMap<Int, Pair<LocalCardModel, ArrayList<LocalCardModel>>>()


    private val _allAnswers = MutableStateFlow(0)
    val allAnswers = _allAnswers.asStateFlow()

    private val _answerCard = MutableStateFlow<AnswerCards2?>(null)
    val answerCard = _answerCard.asStateFlow()

    private val _isTrueAnswer = MutableStateFlow<Boolean?>(null)

    private var job: Job? = null

    fun reset() {
        _timer.value = MAX_TIME
        _allAnswers.value = 0
        _trueAnswers.value = 0
        _answersList.clear()
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
            provider.moduleCard.getRandomModuleView(it)?.card
        }.ifNull {
            provider.cards.getRandomView()
        }?.let {
            LocalCardModel(it)
        }.ifNull { return@launch }

        val s = if (Math.random() > 0.5)
            module.value?.let {
                provider.moduleCard.getRandomModuleView(it)?.card
            }.ifNull {
                provider.cards.getRandomView()
            }?.let {
                LocalCardModel(it)
            }.ifNull { return@launch }
        else f

        _answerCard.value = AnswerCards2(f, s)
    }

    fun check(card:AnswerCards2,b: Boolean): Boolean? {
        val r = (card.first == card.second) == b

        val list = _answerMap[card.first.card.id]?.second.ifNull {
            val l = ArrayList<LocalCardModel>()
            _answerMap[card.first.card.id] = card.first to l
            return@ifNull l
        }
        if (!r) list.add(card.second)
        if (r) _trueAnswers.value++
        _allAnswers.value++
        _isTrueAnswer.value = r
        //newAnswer()
        return r
    }
}
