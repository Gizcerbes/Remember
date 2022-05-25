package com.uogames.remembercards.ui.gameYesOrNo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.uogames.dto.Card
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.collections.ArrayList

class GameYesOrNotViewModel @Inject constructor(
    private val provider: DataProvider
) : ViewModel() {

    companion object {
        const val MAX_TIME = 60
    }

    data class AnswersCard(val firs: Card, val second: Card, var truth: Boolean)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val _timer = MutableStateFlow(MAX_TIME)
    val time = _timer.asStateFlow()

    private val _allAnswers = MutableStateFlow(0)
    val allAnswers = _allAnswers.asStateFlow()

    private val _trueAnswers = MutableStateFlow(0)
    val trueAnswers = _trueAnswers.asStateFlow()

    private val answersList = ArrayList<AnswersCard>()

    private var job: Job? = null

    fun reset() {
        _timer.value = MAX_TIME
        _allAnswers.value = 0
        _trueAnswers.value = 0
        answersList.clear()
    }

    fun start(endCall: () -> Unit) {
        Log.e("TAG", "start: ", )
        job?.cancel()
        job = ioScope.launch {
            while (true) {
                delay(1000)
                _timer.value -= 1
                if (_timer.value == 0) {
                    launch(Dispatchers.Main) { endCall() }
                    return@launch
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun getRandomAnswerCard(call: (AnswersCard) -> Unit) {
        ioScope.launch {
            val firsCard: Card = provider.cards.getRandomCardAsync().await() ?: Card()
            val secondCard: Card =
                provider.cards.getRandomCardWithoutAsync(firsCard.phrase, firsCard.translate)
                    .await() ?: Card()
            launch(Dispatchers.Main) { call(AnswersCard(firsCard, secondCard, false)) }
        }
    }

    fun setAnswer(answer: AnswersCard) {
        answersList.add(answer)
        if (answer.truth) _trueAnswers.value++
        _allAnswers.value++
    }

}