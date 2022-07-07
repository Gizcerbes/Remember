package com.uogames.remembercards.ui.gameYesOrNo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Module
import com.uogames.dto.ModuleCard
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toCard
import com.uogames.repository.DataProvider.Companion.toModule
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

	data class AnswersCard(val firs: Card, val second: Card, var truth: Boolean)

	//private val ioScope = CoroutineScope(Dispatchers.IO)

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

	fun getCard(idCard: Int) = provider.cards.getByIdFlow(idCard)

	fun getPhrase(idPhrase: Int) = provider.phrase.getByIdFlow(idPhrase)

	fun getPronounce(phrase: Phrase) = provider.pronounce.getByPhrase(phrase)

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

	fun getRandomAnswerCard(call: (AnswersCard) -> Unit) {
		viewModelScope.launch {
			val firstModuleCard = module.value?.let {
				provider.moduleCard.getRandomAsync(it).await()?.toCard()
			}.ifNull {
				provider.cards.getRandomAsync().await()
			}.ifNull { return@launch }

			val secondModuleCard = module.value?.let {
				provider.moduleCard.getRandomWithoutAsync(it, firstModuleCard.id).await()?.toCard()
			}.ifNull {
				provider.cards.getRandomWithoutAsync(firstModuleCard.id).await()
			}.ifNull { return@launch }

			val answerCard = AnswersCard(firstModuleCard, secondModuleCard, false)
			launch(Dispatchers.Main) { call(answerCard) }
		}
	}

	fun addAnswer(answer: AnswersCard) {
		_answersList.add(answer)
		if (answer.truth) _trueAnswers.value++
		_allAnswers.value++
	}

}