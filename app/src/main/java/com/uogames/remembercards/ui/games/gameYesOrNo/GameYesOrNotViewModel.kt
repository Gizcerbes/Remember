package com.uogames.remembercards.ui.games.gameYesOrNo

import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalCard
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toCard
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.collections.ArrayList

class GameYesOrNotViewModel @Inject constructor(
    private val provider: DataProvider
) : ViewModel() {

    companion object {
        const val MAX_TIME = 60000
    }

    data class AnswersCard(val firs: LocalCard, val second: LocalCard, var truth: Boolean)

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val module: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _timer = MutableStateFlow(MAX_TIME)
    val time = _timer.asStateFlow()

    private val _trueAnswers = MutableStateFlow(0)
    val trueAnswers = _trueAnswers.asStateFlow()

    private val _isStarted = MutableStateFlow(false)
    val isStarted = _isStarted.asStateFlow()

    private val _answersList = ArrayList<AnswersCard>()

    private val _allAnswers = MutableStateFlow(0)
    val allAnswers = _allAnswers.asStateFlow()

    private var job: Job? = null

    fun reset() {
        _timer.value = MAX_TIME
        _allAnswers.value = 0
        _trueAnswers.value = 0
        _answersList.clear()
    }

    fun getAnswer(position: Int) = _answersList[position]

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

    fun getRandomAnswerCard(call: (AnswersCard) -> Unit) = viewModelScope.launch {
        val firstModuleCard = module.value?.let {
            provider.moduleCard.getRandom(it)?.toCard()
        }.ifNull {
            provider.cards.getRandom()
        }.ifNull { return@launch }

        val secondModuleCard = module.value?.let {
            provider.moduleCard.getRandomWithout(it, firstModuleCard.id)?.toCard()
        }.ifNull {
            provider.cards.getRandomWithout(firstModuleCard.id)
        }.ifNull { return@launch }

        val answerCard = AnswersCard(firstModuleCard, secondModuleCard, false)
        launch(Dispatchers.Main) { call(answerCard) }
    }

    fun addAnswer(answer: AnswersCard) {
        _answersList.add(answer)
        if (answer.truth) _trueAnswers.value++
        _allAnswers.value++
    }
}